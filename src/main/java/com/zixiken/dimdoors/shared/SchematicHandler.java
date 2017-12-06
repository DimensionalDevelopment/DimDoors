/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

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
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    private PocketTemplate personalPocketTemplate;
    private PocketTemplate publicPocketTemplate;
    private List<PocketTemplate> dungeonTemplates; //@todo should this be a Map? Does it need to? It gets reloaded from scratch on ServerStart every time, so...
    final private Map<String, Map<String, Integer>> dungeonNameMap = new HashMap<>();
    //@todo, sort templates by depth over here? that'd mean that that doesn't have to be done on pocket placement each and every time

    PocketTemplate getRandomDungeonPocketTemplate(int depth, int maxPocketSize) { //@todo maxPocketSize is passed for no reason at all here; pockets exceeding maxPocketSize have not been loaded in the first place...
        List<PocketTemplate> validTemplates = new ArrayList<>();
        int totalWeight = 0;
        for (PocketTemplate template : dungeonTemplates) {
            if (depth >= template.getMinDepth() && depth <= template.getMaxDepth()) {
                validTemplates.add(template);
                totalWeight += template.getWeight(depth);
            }
        }
        DimDoors.log(getClass(), "depth = " + depth + ". totalWeight = " + totalWeight);

        Random random = new Random();
        int chosenTemplatePointer = random.nextInt(totalWeight);
        for (PocketTemplate template : validTemplates) {
            chosenTemplatePointer -= template.getWeight(depth);
            if (chosenTemplatePointer < 0) {
                return template;
            }
        }
        DimDoors.warn(getClass(), "No valid dungeon could be chosen for this depth. What have you done to make this happen? Now crashing:");
        return null;
    }

    public void loadSchematics() {
        personalPocketTemplate = loadTemplatesFromJson("default_private", PocketRegistry.INSTANCE.getPrivatePocketSize()).get(0);
        publicPocketTemplate = loadTemplatesFromJson("default_public", PocketRegistry.INSTANCE.getPublicPocketSize()).get(0);
        dungeonTemplates = new ArrayList<>();
        List<String> dungeonSchematicNameStrings = DDConfig.getDungeonSchematicNames();
        int maxPocketSize = PocketRegistry.INSTANCE.getMaxPocketSize();
        for (String nameString : dungeonSchematicNameStrings) {
            List<PocketTemplate> templates = loadTemplatesFromJson(nameString, maxPocketSize);
            if (templates != null) {
                for (PocketTemplate template : templates) {
                    if (template != null && template.getSchematic() != null) {
                        dungeonTemplates.add(template);
                    }
                }
            }
        }
        constructDungeonNameMap();

        // Schematic.tempGenerateDefaultSchematics();
    }

    private void constructDungeonNameMap() {
        //init
        dungeonNameMap.clear();
        //to prevent having to use too many getters
        String bufferedDirectory = null;
        Map<String, Integer> bufferedMap = null;

        for (PocketTemplate template : dungeonTemplates) {
            String dirName = template.getDirName();
            if (dirName != null && dirName.equals(bufferedDirectory)) { //null check not needed
                bufferedMap.put(template.getName(), dungeonTemplates.indexOf(template));
            } else {
                bufferedDirectory = dirName;
                if (dungeonNameMap.containsKey(dirName)) { //this will only happen if you have two json files referring to the same directory being loaded non-consecutively
                    bufferedMap = dungeonNameMap.get(dirName);
                    bufferedMap.put(template.getName(), dungeonTemplates.indexOf(template));
                } else {
                    bufferedMap = new HashMap<>();
                    bufferedMap.put(template.getName(), dungeonTemplates.indexOf(template));
                    dungeonNameMap.put(dirName, bufferedMap);
                }
            }
        }
    }

    private static List<PocketTemplate> loadTemplatesFromJson(String nameString, int maxPocketSize) { //depending on the "jSonType" value in the jSon, this might load several variations of a pocket at once, hence loadTemplate -->s<--
        InputStream jsonJarStream = DimDoors.class.getResourceAsStream("/assets/dimdoors/pockets/json/" + nameString + ".json");
        String schematicJarDirectory = "/assets/dimdoors/pockets/schematic/";
        //init jsons config folder
        File jsonFolder = new File(DDConfig.configurationFolder, "/Jsons");
        if (!jsonFolder.exists()) {
            jsonFolder.mkdirs();
        }
        File jsonFile = new File(jsonFolder, "/" + nameString + ".json"); //@todo this could probably be moved a few lines down
        //init schematics config folder
        File schematicFolder = new File(DDConfig.configurationFolder, "/Schematics");
        if (!schematicFolder.exists()) {
            schematicFolder.mkdirs();
        }

        //load the json and convert it to a JsonObject
        String jsonString;
        if (jsonJarStream != null) {
            StringWriter writer = new StringWriter();
            try {
                IOUtils.copy(jsonJarStream, writer, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Json-file " + nameString + ".json did not load correctly from jar. Skipping loading of this template.", ex);
                return new ArrayList<>();
            }
            jsonString = writer.toString();
        } else if (jsonFile.exists()) {
            DimDoors.log(SchematicHandler.class, "Json-file " + nameString + ".json was not found in the jar. Loading from config directory instead.");
            try {
                jsonString = readFile(jsonFile.getAbsolutePath(), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Json-file " + nameString + ".json did not load correctly from config folder. Skipping loading of this template.", ex);
                return new ArrayList<>();
            }
        } else {
            DimDoors.warn(SchematicHandler.class, "Json-file " + nameString + ".json was not found in the jar or config directory. Skipping loading of this template.");
            return new ArrayList<>();
        }
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        JsonObject jsonTemplate = jsonElement.getAsJsonObject();
        //DimDoors.log(SchematicHandler.class, "Checkpoint 1 reached");

        //Generate and get templates (without a schematic) of all variations that are valid for the current "maxPocketSize" 
        List<PocketTemplate> validTemplates = getAllValidVariations(jsonTemplate, maxPocketSize);
        //DimDoors.log(SchematicHandler.class, "Checkpoint 4 reached; " + validTemplates.size() + " templates were loaded");

        String subDirectory = jsonTemplate.get("directory").getAsString(); //get the subfolder in which the schematics are stored

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
                DimDoors.log(SchematicHandler.class, "Schematic " + template.getName() + " was bigger than specified in " + nameString + ".json and therefore wasn't loaded");
            }
            template.setSchematic(schematic);
        }
        return validTemplates;
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static List<PocketTemplate> getAllValidVariations(JsonObject jsonTemplate, int maxPocketSize) {
        final String directory = jsonTemplate.get("directory").getAsString();
        final String jsonType = jsonTemplate.get("jsonType").getAsString();
        final EnumPocketType pocketType = EnumPocketType.getFromInt(jsonTemplate.get("pocketType").getAsInt());
        final JsonArray variations = jsonTemplate.getAsJsonArray("variations");

        List<PocketTemplate> pocketTemplates = new ArrayList<>();
        JsonObject chosenVariation = null; //only applicable if jsonType == "Singular"
        int chosenVariationSize = -1; //only applicable if jsonType == "Singular"
        List<JsonObject> validVariations = new ArrayList<>();
        //put all valid variation JsonObjects into an array list
        for (int i = 0; i < variations.size(); i++) {
            JsonObject variation = variations.get(i).getAsJsonObject();
            int variationSize = variation.get("size").getAsInt();

            if (variationSize > maxPocketSize) {
                //DimDoors.log(SchematicHandler.class, "Checkpoint 2 reached; Variation size " + variationSize + " is bigger than maxPocketSize " + maxPocketSize + ".");
                //do not add it
            } else if (jsonType.equals("Singular")) {
                if (variationSize > chosenVariationSize) {
                    chosenVariationSize = variationSize;
                    chosenVariation = variation;
                    if (variationSize == maxPocketSize) {
                        break; //this one gets chosen
                    }
                }
            } else if (jsonType.equals("Multiple")) {
                validVariations.add(variation);
            } else { //@todo more options?
                DimDoors.log(SchematicHandler.class, "JsonType " + jsonType + " is not a valid JsonType. Json was not loaded.");
            }
        }
        if (chosenVariation != null) {
            validVariations.add(chosenVariation);
        }
        //DimDoors.log(SchematicHandler.class, "Checkpoint 3 reached; " + validVariations.size() + " variations were selected.");

        //convert the valid variations arraylist to a list of pocket templates
        for (JsonObject variation : validVariations) {
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

    /**
     * @return the personalPocketTemplate
     */
    public PocketTemplate getPersonalPocketTemplate() {
        return personalPocketTemplate;
    }

    /**
     * @return the publicPocketTemplate
     */
    public PocketTemplate getPublicPocketTemplate() {
        return publicPocketTemplate;
    }

    /**
     * @param directory file directory of the template schematic
     * @param name filename of the template schematic
     * @return the dungeonTemplate that was loaded from folder {@code directory}
     * with filename {@code name}
     */
    public PocketTemplate getDungeonTemplate(String directory, String name) {
        //@todo nullcheck on the parameters
        int index = dungeonNameMap.get(directory).get(name);
        return dungeonTemplates.get(index);
    }

    public ArrayList<String> getDungeonTemplateGroups() {
        return new ArrayList<>(dungeonNameMap.keySet());
    }

    public ArrayList<String> getDungeonTemplateNames(String directory) {
        return new ArrayList<>(dungeonNameMap.get(directory).keySet());
    }

    /**
     * @return the dungeonTemplates
     */
    public List<PocketTemplate> getDungeonTemplates() {
        return dungeonTemplates;
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
