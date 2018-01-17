package org.dimdev.dimdoors.shared.pockets;

import org.dimdev.dimdoors.shared.Config;
import org.dimdev.ddutils.math.MathUtils;
import org.dimdev.ddutils.schem.Schematic;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dimdev.dimdoors.DimDoors;
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

import org.dimdev.dimdoors.shared.tools.SchematicConverter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import org.apache.commons.io.IOUtils;
import org.dimdev.dimdoors.shared.world.ModDimensions;

/**
 * @author Robijnvogel
 */
public class SchematicHandler { // TODO: make this more general (not dimdoors-related)

    public static final SchematicHandler INSTANCE = new SchematicHandler(); // TODO: make static

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
        File jsonFolder = new File(Config.configurationFolder, "/jsons");
        if (!jsonFolder.exists()) {
            jsonFolder.mkdirs();
        }
        // Init schematics config folder
        File schematicFolder = new File(Config.configurationFolder, "/schematics");
        if (!schematicFolder.exists()) {
            schematicFolder.mkdirs();
        }

        for (File file : jsonFolder.listFiles()) {
            try {
                String jsonString = IOUtils.toString(file.toURI(), StandardCharsets.UTF_8);
                templates.addAll(loadTemplatesFromJson(jsonString));
            } catch (IOException e) {
                DimDoors.log.error("Error reading file " + file.toURI() + ". The following exception occured: ", e);
            }
        }
        constructNameMap();

        DimDoors.log.info("Loaded " + templates.size() + " templates.");
    }

    private static List<PocketTemplate> loadTemplatesFromJson(String jsonString) {
        String schematicJarDirectory = "/assets/dimdoors/pockets/schematic/";
        File schematicFolder = new File(Config.configurationFolder, "/schematics");

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        JsonObject jsonTemplate = jsonElement.getAsJsonObject();

        //Generate and get templates (without a schematic) of all variations that are valid for the current "maxPocketSize" 
        List<PocketTemplate> validTemplates = getAllValidVariations(jsonTemplate);

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
                    DimDoors.log.error("Schematic file " + template.getName() + ".schem did not load correctly from config folder.", ex);
                }
            } else if (oldVersionSchematicFile.exists()) {
                try {
                    schematicDataStream = new DataInputStream(new FileInputStream(oldVersionSchematicFile));
                    streamOpened = true;
                } catch (FileNotFoundException ex) {
                    DimDoors.log.error("Schematic file " + template.getName() + ".schematic did not load correctly from config folder.", ex);
                }
            } else {
                DimDoors.log.warn("Schematic '" + template.getName() + "' was not found in the jar or config directory, neither with the .schem extension, nor with the .schematic extension.");
            }

            NBTTagCompound schematicNBT;
            Schematic schematic = null;
            if (streamOpened) {
                try {
                    schematicNBT = CompressedStreamTools.readCompressed(schematicDataStream);
                    if (!schematicNBT.hasKey("Version")) {
                        schematic = SchematicConverter.convertSchematic(schematicNBT, template.getName());
                    } else {
                        schematic = Schematic.loadFromNBT(schematicNBT, template.getName());
                    }
                    schematicDataStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Schematic file for " + template.getName() + " could not be read as a valid schematic NBT file.", ex); // TODO: consistently use one type of logger for this.
                } finally {
                    try {
                        schematicDataStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Error occured while closing schematicDataStream", ex);
                    }
                }
            }

            if (schematic != null
                    && (schematic.width > (template.getSize() + 1) * 16 || schematic.length > (template.getSize() + 1) * 16)) {
                schematic = null;
                DimDoors.log.warn("Schematic " + template.getName() + " was bigger than specified in its json file and therefore wasn't loaded");
            }
            template.setSchematic(schematic);
            // TODO: delete from validTemplates if schematic is null
        }
        return validTemplates;
    }

    private static List<PocketTemplate> getAllValidVariations(JsonObject jsonTemplate) {
        List<PocketTemplate> pocketTemplates = new ArrayList<>();

        final String directory = jsonTemplate.get("group").getAsString();
        int maxSize = -1;
        if (!Config.isLoadAllSchematics()) {
            switch (directory) {
                case "public":
                    maxSize = Config.getPublicPocketSize(); // TODO: hardcode?¿
                    break;
                case "private":
                    maxSize = Config.getPrivatePocketSize();
                    break;
                default:
                    maxSize = Config.getMaxPocketSize();
                    break;
            }
        }

        final JsonArray variations = jsonTemplate.getAsJsonArray("variations");

        //convert the variations arraylist to a list of pocket templates
        for (JsonElement variationElement : variations) {
            JsonObject variation = variationElement.getAsJsonObject();
            String variantName = variation.get("variantName").getAsString();
            int variationSize = variation.get("size").getAsInt();
            if (maxSize >= 0 && variationSize > maxSize) {
                continue;
            }
            int minDepth = variation.get("minDepth").getAsInt();
            int maxDepth = variation.get("maxDepth").getAsInt();
            int baseWeight = variation.get("baseWeight").getAsInt();
            PocketTemplate pocketTemplate = new PocketTemplate(directory, variantName, variationSize, minDepth, maxDepth, baseWeight);
            pocketTemplates.add(pocketTemplate);
        }

        return pocketTemplates;
    }

    private void constructNameMap() {
        nameMap = new HashMap<>();
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
    public Set<String> getTemplateGroups() {
        return nameMap.keySet();
    }

    public Set<String> getTemplateNames(String group) {
        if (nameMap.containsKey(group)) {
            return nameMap.get(group).keySet();
        } else {
            return new HashSet<>();
        }
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
        if (groupMap == null) {
            return null;
        }
        Integer index = groupMap.get(name);
        if (index == null) {
            return null;
        }
        return templates.get(index);
    }

    /**
     * Gets a random template matching certain criteria.
     *
     * @return A random template matching those criteria, or null if none were found
     */
    public PocketTemplate getRandomTemplate(Map<String, Float> groupWeights, int depth, int maxSize, boolean getLargest) { // TODO: useful?
        String group = MathUtils.weightedRandom(groupWeights);
        return getRandomTemplate(group, depth, maxSize, getLargest);
    }

    /**
     * Gets a random template matching certain criteria.
     *
     * @param maxSize Maximum size the template can be.
     * @param getLargest Setting this to true will always get the largest template size in that group, 
     * but still randomly out of the templates with that size (ex. for private and public pockets)
     * @return A random template matching those criteria, or null if none were found
     */
    public PocketTemplate getRandomTemplate(String group, int depth, int maxSize, boolean getLargest) {
        // TODO: cache this for faster calls:
        Map<PocketTemplate, Float> weightedTemplates = new HashMap<>();
        int largestSize = 0;
        for (PocketTemplate template : templates) {
            if (template.getGroupName().equals(group)
                    && (depth == -1 || depth >= template.getMinDepth() && (depth <= template.getMaxDepth() || template.getMaxDepth() == -1))
                    && (maxSize == -1 || template.getSize() <= maxSize)) {
                if (getLargest && template.getSize() > largestSize) {
                    weightedTemplates = new HashMap<>();
                    largestSize = template.getSize();
                }
                weightedTemplates.put(template, template.getWeight(depth));
            }
        }
        if (weightedTemplates.isEmpty()) {
            DimDoors.log.warn("getRandomTemplate failed, no templates matching those criteria were found.");
            return null; // TODO: switch to exception system
        }

        return MathUtils.weightedRandom(weightedTemplates);
    }

    public PocketTemplate getPersonalPocketTemplate() {
        return getRandomTemplate("private", -1, Math.min(Config.getPrivatePocketSize(), PocketRegistry.instance(ModDimensions.getPrivateDim()).getPrivatePocketSize()), true);
    }

    public PocketTemplate getPublicPocketTemplate() {
        return getRandomTemplate("public", -1, Math.min(Config.getPublicPocketSize(), PocketRegistry.instance(ModDimensions.getPublicDim()).getPublicPocketSize()), true);
    }

    public void saveSchematic(Schematic schematic, String name) {
        NBTTagCompound schematicNBT = Schematic.saveToNBT(schematic);
        File saveFolder = new File(Config.configurationFolder, "/Schematics/Saved");
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
