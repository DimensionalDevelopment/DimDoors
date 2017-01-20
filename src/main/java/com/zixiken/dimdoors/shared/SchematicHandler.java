/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DDConfig;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robijnvogel
 */
public class SchematicHandler {

    public static final SchematicHandler Instance = new SchematicHandler();
    private PocketPlacer personalPocketSchematic;
    private PocketPlacer publicPocketSchematic;
    private List<PocketPlacer> dungeonSchematics;

    private SchematicHandler() {
        loadSchematics();
    }

    PocketPlacer getPersonalPocketSchematic(int maxPocketSize) {
        return personalPocketSchematic;
    }

    PocketPlacer getPublicPocketSchematic(int maxPocketSize) {
        return publicPocketSchematic;
    }

    PocketPlacer getRandomDungeonSchematic(int depth, int maxPocketSize) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadSchematics() {
        personalPocketSchematic = loadSchematic("defaultPersonal", PocketRegistry.Instance.getPrivatePocketSize());
        publicPocketSchematic = loadSchematic("defaultPublic", PocketRegistry.Instance.getPublicPocketSize());
        dungeonSchematics = new ArrayList();
        List<String> dungeonSchematicNameStrings = DDConfig.getDungeonSchematicNames(); //@todo load default dungeon schematics AND user-added schematics
        for (String nameString : dungeonSchematicNameStrings) {
            PocketPlacer schematic = loadSchematic(nameString, PocketRegistry.Instance.getMaxPocketSize()); //should keep in mind the globally set maximum schematic size
            if (schematic != null) {
                dungeonSchematics.add(schematic);
            }
        }
    }

    private PocketPlacer loadSchematic(String nameString, int maxPocketSize) {
        //check for json files in both directories (inside the mod jar, and inside the dimdoors config folder)
        //check if the json has a "variant" with the correct pocket size, if it doesn't, pick the largest smaller variant. If there's only bigger variants, cancel
    }

}
