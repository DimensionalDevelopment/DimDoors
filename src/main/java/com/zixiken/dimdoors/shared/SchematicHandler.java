/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DDConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Robijnvogel
 */
public class SchematicHandler {

    public static final SchematicHandler Instance = new SchematicHandler();
    private PocketTemplate personalPocketSchematic;
    private PocketTemplate publicPocketSchematic;
    private List<PocketTemplate> dungeonSchematics;

    PocketTemplate getPersonalPocketSchematic(int maxPocketSize) {
        return personalPocketSchematic;
    }

    PocketTemplate getPublicPocketSchematic(int maxPocketSize) {
        return publicPocketSchematic;
    }

    PocketTemplate getRandomDungeonPocketTemplate(int depth, int maxPocketSize) {
        List<PocketTemplate> validTemplates = new ArrayList();
        int totalWeight = 0;
        for (PocketTemplate template : dungeonSchematics) {
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
        personalPocketSchematic = loadSchematicsFromJson("defaultPersonal", PocketRegistry.Instance.getPrivatePocketSize()).get(0);
        publicPocketSchematic = loadSchematicsFromJson("defaultPublic", PocketRegistry.Instance.getPublicPocketSize()).get(0);
        dungeonSchematics = new ArrayList();
        List<String> dungeonSchematicNameStrings = DDConfig.getDungeonSchematicNames();
        int maxPocketSize = PocketRegistry.Instance.getMaxPocketSize();
        for (String nameString : dungeonSchematicNameStrings) {
            List<PocketTemplate> schematics = loadSchematicsFromJson(nameString, maxPocketSize);
            if (schematics != null) {
                for (PocketTemplate schematic : schematics) {
                    dungeonSchematics.add(schematic);
                }
            }
        }
    }

    private List<PocketTemplate> loadSchematicsFromJson(String nameString, int maxPocketSize) { //depending on the "jSonType" value in the jSon, this might load several variations of a pocket at once
        //check for json files in both directories (inside the mod jar, and inside the dimdoors config folder)
        //check if the json has a "variant" with the correct pocket size, if it doesn't, pick the largest smaller variant. If there's only bigger variants, cancel
    }
}
