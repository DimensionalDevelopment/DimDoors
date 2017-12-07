/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.util.MathUtils;
import com.zixiken.dimdoors.shared.util.Schematic;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zixiken.dimdoors.DimDoors;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Robijnvogel
 */
public class SchematicHandler {
    public static final SchematicHandler INSTANCE = new SchematicHandler();

    private List<PocketTemplate> templates;
    private Map<String, Map<String, Integer>> nameMap; // group -> name -> index in templates

    // LOADING CODE STARTS HERE <editor-fold>

    public void loadSchematics() {
        templates = new ArrayList<>();

        String[] names = {"default_dungeon_normal", "default_dungeon_nether", "default_private", "default_public"}; // TODO: don't hardcode
        for (String name : names) {
            try {
                URL resource = DimDoors.class.getResource("/assets/dimdoors/pockets/json/" + name + ".json");
                String jsonString = IOUtils.toString(resource, StandardCharsets.UTF_8);
                templates.addAll(loadTemplatesFromJson(jsonString));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Load config jsons
        File jsonFolder = new File(DDConfig.configurationFolder, "/jsons");
        if (!jsonFolder.exists()) {
            jsonFolder.mkdirs();
        }
        // Init schematics config folder
        File schematicFolder = new File(DDConfig.configurationFolder, "/schematics");
        if (!schematicFolder.exists()) {
            schematicFolder.mkdirs();
        }

        for (File file : jsonFolder.listFiles()) {
            try {
                String jsonString = IOUtils.toString(file.toURI(), StandardCharsets.UTF_8);
                templates.addAll(loadTemplatesFromJson(jsonString));
            } catch (IOException e) {
                DimDoors.warn("Error reading file " + file.toURI() + ". The following exception occured: ");
            }
        }
        constructNameMap();

        DimDoors.log("Loaded " + templates.size() + " templates.");
    }

    private static List<PocketTemplate> loadTemplatesFromJson(String jsonString) {
        String schematicJarDirectory = "/assets/dimdoors/pockets/schematic/";
        File schematicFolder = new File(DDConfig.configurationFolder, "/schematics");

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        JsonObject jsonTemplate = jsonElement.getAsJsonObject();
        //DimDoors.log(SchematicHandler.class, "Checkpoint 1 reached");

        //Generate and get templates (without a schematic) of all variations that are valid for the current "maxPocketSize" 
        List<PocketTemplate> validTemplates = getAllValidVariations(jsonTemplate);
        //DimDoors.log(SchematicHandler.class, "Checkpoint 4 reached; " + validTemplates.size() + " templates were loaded");

        String subDirectory = jsonTemplate.get("group").getAsString(); //get the subfolder in which the schematics are stored

        for (PocketTemplate template : validTemplates) { //it's okay to "tap" this for-loop, even if validTemplates is empty.
            String extendedTemplatelocation = subDirectory.equals("") ? template.getName() : subDirectory + "/" + template.getName(); //transform the filename accordingly

            //Initialising the possible locations/formats for the schematic file
            InputStream schematicStream = DimDoors.class.getResourceAsStream(schematicJarDirectory + extendedTemplatelocation + ".schem");
            InputStream oldVersionSchematicStream = DimDoors.class.getResourceAsStream(schematicJarDirectory + extendedTemplatelocation + ".schematic"); //@todo also check for other schematics
            File schematicFile = new File(schematicFolder, "/" + extendedTemplatelocation + ".schem");
            File oldVersionSchematicFile = new File(schematicFolder, "/" + extendedTemplatelocation + ".schematic");

            //determine which location to load the schematic file from (and what format)
            DataInputStream schematicDataStream = null;
            boolean streamOpened = false;
            if (schematicStream != null) {
                schematicDataStream = new DataInputStream(schematicStream);
                streamOpened = true;
            } else if (oldVersionSchematicStream != null) {
                schematicDataStream = new DataInputStream(oldVersionSchematicStream);
                streamOpened = true;
            } else if (schematicFile.exists()) {
                try {
                    schematicDataStream = new DataInputStream(new FileInputStream(schematicFile));
                    streamOpened = true;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Schematic file " + template.getName() + ".schem did not load correctly from config folder.", ex);
                }
            } else if (oldVersionSchematicFile.exists()) {
                try {
                    schematicDataStream = new DataInputStream(new FileInputStream(oldVersionSchematicFile));
                    streamOpened = true;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Schematic file " + template.getName() + ".schematic did not load correctly from config folder.", ex);
                }
            } else {
                DimDoors.warn(SchematicHandler.class, "Schematic '" + template.getName() + "' was not found in the jar or config directory, neither with the .schem extension, nor with the .schematic extension.");
            }

            NBTTagCompound schematicNBT;
            Schematic schematic = null;
            if (streamOpened) {
                try {
                    schematicNBT = CompressedStreamTools.readCompressed(schematicDataStream);
                    schematic = Schematic.loadFromNBT(schematicNBT, template.getName());
                    schematicDataStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Schematic file for " + template.getName() + " could not be read as a valid schematic NBT file.", ex);
                } finally {
                    try {
                        schematicDataStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Error occured while closing schematicDataStream", ex);
                    }
                }
            }

            if (schematic != null
                    && (schematic.getWidth() > (template.getSize() + 1) * 16 || schematic.getLength() > (template.getSize() + 1) * 16)) {
                schematic = null;
                DimDoors.log(SchematicHandler.class, "Schematic " + template.getName() + " was bigger than specified in its json file and therefore wasn't loaded");
            }
            template.setSchematic(schematic);
        }
        return validTemplates;
    }

    private static List<PocketTemplate> getAllValidVariations(JsonObject jsonTemplate) {
        List<PocketTemplate> pocketTemplates = new ArrayList<>();

        final String directory = jsonTemplate.get("group").getAsString();
        final EnumPocketType pocketType = EnumPocketType.getFromInt(jsonTemplate.get("pocketType").getAsInt());
        final JsonArray variations = jsonTemplate.getAsJsonArray("variations");

        //convert the variations arraylist to a list of pocket templates
        for (JsonElement variationElement : variations) {
            JsonObject variation = variationElement.getAsJsonObject();
            String variantName = variation.get("variantName").getAsString();
            int variationSize = variation.get("size").getAsInt();
            int minDepth = variation.get("minDepth").getAsInt();
            int maxDepth = variation.get("maxDepth").getAsInt();
            JsonArray weightsJsonArray = variation.get("weights").getAsJsonArray();
            int[] weights = new int[weightsJsonArray.size()];
            for (int j = 0; j < weightsJsonArray.size(); j++) {
                weights[j] = weightsJsonArray.get(j).getAsInt();
            }
            PocketTemplate pocketTemplate = new PocketTemplate(directory, variantName, variationSize, pocketType, minDepth, maxDepth, weights);
            pocketTemplates.add(pocketTemplate);
        }

        return pocketTemplates;
    }

    private void constructNameMap() {
        nameMap = new HashMap<String, Map<String, Integer>>();
        //to prevent having to use too many getters
        String bufferedDirectory = null;
        Map<String, Integer> bufferedMap = null;

        for (PocketTemplate template : templates) {
            String dirName = template.getGroupName();
            if (dirName != null && dirName.equals(bufferedDirectory)) { //null check not needed
                bufferedMap.put(template.getName(), templates.indexOf(template));
            } else {
                bufferedDirectory = dirName;
                if (nameMap.containsKey(dirName)) { //this will only happen if you have two json files referring to the same directory being loaded non-consecutively
                    bufferedMap = nameMap.get(dirName);
                    bufferedMap.put(template.getName(), templates.indexOf(template));
                } else {
                    bufferedMap = new HashMap<>();
                    bufferedMap.put(template.getName(), templates.indexOf(template));
                    nameMap.put(dirName, bufferedMap);
                }
            }
        }
    }

    // LOADING CODE ENDS HERE </editor-fold>

    public ArrayList<String> getTemplateGroups() {
        return new ArrayList<>(nameMap.keySet());
    }

    public ArrayList<String> getTemplateNames(String group) {
        return new ArrayList<>(nameMap.get(group).keySet());
    }

    /**
     * Gets a loaded PocketTemplate by its group and name.
     *
     * @param group Template group
     * @param name Template name
     * @return The dungeon template with that group and name, or null if it wasn't found
     */
    public PocketTemplate getTemplate(String group, String name) {
        Map<String, Integer> groupMap = nameMap.get(group);
        if(groupMap == null) return null;
        Integer index = groupMap.get(name);
        if(index == null) return null;
        return templates.get(index);
    }


    /**
     * Gets a random template matching certain criteria.
     *
     * @param groupWeights
     * @param depth
     * @param maxSize
     * @param getLargest
     * @return A random template matching those criteria, or null if none were found
     */
    public PocketTemplate getRandomTemplate(Map<String, Integer> groupWeights, int depth, int maxSize, boolean getLargest) { // TODO: useful?
        String group = MathUtils.weightedRandom(groupWeights);
        return getRandomTemplate(group, depth, maxSize, getLargest);
    }

    /**
     * Gets a random template matching certain criteria.
     *
     * @param group
     * @param depth
     * @param maxSize Maximum size the template can be.
     * @param getLargest Setting this to true will always get the largest template size in that group, but still randomly out of the templates with that size (ex. for private pockets)
     * @return A random template matching those criteria, or null if none were found
     */
    public PocketTemplate getRandomTemplate(String group, int depth, int maxSize, boolean getLargest) {
        // TODO: cache this for faster calls:
        Map<PocketTemplate, Integer> weightedTemplates = new HashMap<>();
        int largestSize = 0;
        for (PocketTemplate template : templates) {
            if (template.getGroupName().equals(group)
                    && (depth == -1 || depth >= template.getMinDepth() && (depth <= template.getMaxDepth() || template.getMaxDepth() == -1))
                    && (maxSize == -1 || template.getSize() < maxSize)) {
                if (getLargest && template.getSize() > largestSize) {
                    weightedTemplates = new HashMap<>();
                    largestSize = template.getSize();
                }
                weightedTemplates.put(template, template.getWeight(depth));
            }
        }
        if (weightedTemplates.size() == 0) {
            DimDoors.warn("getRandomTemplate failed, no templates matching those criteria were found.");
            return null; // TODO: switch to exception system
        }

        return MathUtils.weightedRandom(weightedTemplates);
    }

    public PocketTemplate getPersonalPocketTemplate() {
        return getRandomTemplate("private", -1, DDConfig.getMaxPocketSize(), true); // TODO: config option for getLargest
    }

    public PocketTemplate getPublicPocketTemplate() {
        return getRandomTemplate("private", -1, DDConfig.getMaxPocketSize(), true); // TODO: config option for getLargest
    }

    public PocketTemplate getDungeonTemplate(float netherProbability, int depth) {
        Random random = new Random();
        String group = (random.nextFloat() < netherProbability) ? "nether" : "ruins";
        return getRandomTemplate(group, depth, DDConfig.getMaxPocketSize(), false);
    }

    public void saveSchematic(Schematic schematic, String name) {
        NBTTagCompound schematicNBT = Schematic.saveToNBT(schematic);
        File saveFolder = new File(DDConfig.configurationFolder, "/Schematics/Saved");
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }

        File saveFile = new File(saveFolder.getAbsolutePath() + "/" + name + ".schem");
        try {
            saveFile.createNewFile();
            DataOutputStream schematicDataStream = new DataOutputStream(new FileOutputStream(saveFile));
            CompressedStreamTools.writeCompressed(schematicNBT, schematicDataStream);
            schematicDataStream.flush();
            schematicDataStream.close();
        } catch (IOException ex) {
            Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Something went wrong while saving " + saveFile.getAbsolutePath() + " to disk.", ex);
        }
    }
}
