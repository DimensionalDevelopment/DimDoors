/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zixiken.dimdoors.DDConfig;
import com.zixiken.dimdoors.DimDoors;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;

/**
 *
 * @author Robijnvogel
 */
public class SchematicHandler {

    public static final SchematicHandler Instance = new SchematicHandler();
    private PocketTemplate personalPocketTemplate;
    private PocketTemplate publicPocketTemplate;
    private List<PocketTemplate> dungeonTemplates;
    //@todo, sort templates by depth over here? that'd mean that that doesn't have to be done on pocket placement each and every time

    PocketTemplate getPersonalPocketSchematic(int maxPocketSize) {
        return personalPocketTemplate;
    }

    PocketTemplate getPublicPocketSchematic(int maxPocketSize) {
        return publicPocketTemplate;
    }

    PocketTemplate getRandomDungeonPocketTemplate(int depth, int maxPocketSize) {
        List<PocketTemplate> validTemplates = new ArrayList();
        int totalWeight = 0;
        for (PocketTemplate template : dungeonTemplates) {
            if (template.getMinDepth() > depth || template.getMaxDepth() < depth) {
                //do nothing
            } else {
                validTemplates.add(template);
                totalWeight += template.getWeight(depth);
            }
        }

        Random random = new Random();
        int chosenTemplatePointer = random.nextInt(totalWeight);
        for (PocketTemplate template : validTemplates) {
            if (chosenTemplatePointer < 0) {
                return template;
            }
            chosenTemplatePointer -= template.getWeight(depth);
        }
        return null;
    }

    public void loadSchematics() {
        personalPocketTemplate = loadTemplatesFromJson("defaultPersonal", PocketRegistry.Instance.getPrivatePocketSize()).get(0);
        publicPocketTemplate = loadTemplatesFromJson("defaultPublic", PocketRegistry.Instance.getPublicPocketSize()).get(0);
        dungeonTemplates = new ArrayList();
        List<String> dungeonSchematicNameStrings = DDConfig.getDungeonSchematicNames();
        int maxPocketSize = PocketRegistry.Instance.getMaxPocketSize();
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
    }

    private List<PocketTemplate> loadTemplatesFromJson(String nameString, int maxPocketSize) { //depending on the "jSonType" value in the jSon, this might load several variations of a pocket at once, hence loadTemplate -->s<--
        File jsonFolder = new File(DDConfig.configurationFolder, "/Jsons");
        File jsonFile = new File(jsonFolder, "/" + nameString + ".json");
        File schematicFolder = new File(DDConfig.configurationFolder, "/Schematics");
        String jsonString = null;
        try {
            jsonString = readFile(jsonFile.getAbsolutePath(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Template Json file for template " + nameString + " was not found in template folder.", ex);
        }
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        JsonObject jsonTemplate = jsonElement.getAsJsonObject();
        List<PocketTemplate> validTemplates = getAllValidVariations(jsonTemplate, maxPocketSize);

        for (PocketTemplate template : validTemplates) { //it's okay to "tap" this for-loop, even if validTemplates is empty.
            File schematicFile = new File(schematicFolder, "/" + template.getName() + ".schem");
            NBTTagCompound schematicNBT;
            Schematic schematic = null;
            try {
                schematicNBT = CompressedStreamTools.read(schematicFile);
                schematic = Schematic.loadFromNBT(schematicNBT);
            } catch (IOException ex) {
                Logger.getLogger(SchematicHandler.class.getName()).log(Level.SEVERE, "Schematic file for schematic " + template.getName() + " was not found in template folder.", ex);
            }
            if (schematic != null && (schematic.width > (template.getSize()) * 16 || schematic.length > (template.getSize()) * 16)) {
                schematic = null;
                DimDoors.log(this.getClass(), "Schematic " + template.getName() + ".schem was bigger than specified in " + nameString + ".json and therefore wasn't loaded");
            }
            template.setSchematic(schematic);
        }
        //@todo check for json files in both directories (inside the mod jar, and inside the dimdoors config folder)
        return validTemplates;
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private List<PocketTemplate> getAllValidVariations(JsonObject jsonTemplate, int maxPocketSize) {
        String jsonType = jsonTemplate.get("jsonType").getAsString();
        EnumPocketType pocketType = EnumPocketType.getFromInt(jsonTemplate.get("pocketType").getAsInt());
        JsonArray variations = jsonTemplate.getAsJsonArray("variations");

        List<PocketTemplate> pocketTemplates = new ArrayList();
        if (jsonType.equals("Singular")) { //@todo, make sure there is only one Json-reader block, instead of one and its copy
            JsonObject chosenVariation = null;
            int chosenVariationSize = 0;
            for (int i = 0; i < variations.size(); i++) {
                JsonObject variation = variations.get(i).getAsJsonObject();
                int variationSize = variation.get("size").getAsInt();
                if (variationSize <= maxPocketSize && variationSize > chosenVariationSize) {
                    chosenVariationSize = variationSize;
                    chosenVariation = variation;
                    if (variationSize == maxPocketSize) {
                        break; //this one gets chosen
                    }
                }
            }
            if (chosenVariation != null) {
                //this block equals
                String variantName = chosenVariation.get("variantName").getAsString();
                EnumPocketType typeID = EnumPocketType.getFromInt(chosenVariation.get("typeID").getAsInt());
                int minDepth = chosenVariation.get("minDepth").getAsInt();
                int maxDepth = chosenVariation.get("maxDepth").getAsInt();
                JsonArray weightsJsonArray = chosenVariation.get("weights").getAsJsonArray();
                int[] weights = new int[weightsJsonArray.size()];
                for (int i = 0; i < weightsJsonArray.size(); i++) {
                    weights[i] = weightsJsonArray.get(i).getAsInt();
                }
                PocketTemplate pocketTemplate = new PocketTemplate(variantName, chosenVariationSize, typeID, minDepth, maxDepth, weights);
                pocketTemplates.add(pocketTemplate);
                ///this block equals
            }
        } else if (jsonType.equals("Multiple")) {
            for (int i = 0; i < variations.size(); i++) {
                JsonObject variation = variations.get(i).getAsJsonObject();
                int variationSize = variation.get("size").getAsInt();
                if (variationSize <= maxPocketSize) {
                    //this block
                    String variantName = variation.get("variantName").getAsString();
                    EnumPocketType typeID = EnumPocketType.getFromInt(variation.get("typeID").getAsInt());
                    int minDepth = variation.get("minDepth").getAsInt();
                    int maxDepth = variation.get("maxDepth").getAsInt();
                    JsonArray weightsJsonArray = variation.get("weights").getAsJsonArray();
                    int[] weights = new int[weightsJsonArray.size()];
                    for (int j = 0; j < weightsJsonArray.size(); j++) {
                        weights[j] = weightsJsonArray.get(j).getAsInt();
                    }
                    PocketTemplate pocketTemplate = new PocketTemplate(variantName, variationSize, typeID, minDepth, maxDepth, weights);
                    pocketTemplates.add(pocketTemplate);
                    ///this block
                }
            }
        } //@todo, more options?
        return pocketTemplates;
    }
}
