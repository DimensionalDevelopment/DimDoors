/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 *
 * @author Robijnvogel
 */
public class DDConfig {

    private static int pocketGridSize = 8;
    private static int maxPocketSize = 4;
    private static int privatePocketSize = 3;
    private static int publicPocketSize = 2;
    private static String[] dungeonSchematicNames = {"", ""}; //@todo set default dungeon names

    public static void loadConfig(FMLPreInitializationEvent event) {

        // Load config
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        // Setup general
        //@todo a comment in the config files about how these values only influence new worlds
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "pocketGridSize", pocketGridSize,
                "Sets how many chunks apart all pockets in pocket dimensions should be placed. [min: 4, max: 16, default: 8]", 4, 8);
        pocketGridSize = prop.getInt(pocketGridSize);

        prop = config.get(Configuration.CATEGORY_GENERAL, "maxPocketSize", maxPocketSize,
                "Sets how many deep and wide any pocket can be. [min: 1, max: pocketGridSize/2, default: 4]", 1, (int) (((double) pocketGridSize / 2) - 0.5));
        maxPocketSize = prop.getInt(maxPocketSize);

        prop = config.get(Configuration.CATEGORY_GENERAL, "privatePocketSize", privatePocketSize,
                "Sets how many deep and wide any personal pocket can be. [min: 1, max: maxPocketsSize, default: 3]", 1, maxPocketSize);
        privatePocketSize = prop.getInt(privatePocketSize);

        prop = config.get(Configuration.CATEGORY_GENERAL, "publicPocketSize", publicPocketSize,
                "Sets how many deep and wide any public pocket can be. [min: 1, max: maxPocketsSize, default: 2]", 1, maxPocketSize);
        publicPocketSize = prop.getInt(publicPocketSize);

        prop = config.get(Configuration.CATEGORY_GENERAL, "dungeonSchematicNames", dungeonSchematicNames, 
                "List of names of Pockets' jSon- and Schematic file names excluding extention. Custom json and schematic files can be dropped in the corresponding folders.");
        dungeonSchematicNames = prop.getStringList();
        
        // Save config
        config.save();
    }

    public static int getPocketGridSize() {
        return pocketGridSize;
    }

    public static int getMaxPocketsSize() {
        return maxPocketSize;
    }

    public static int getPrivatePocketSize() {
        return privatePocketSize;
    }

    public static int getPublicPocketSize() {
        return publicPocketSize;
    }

    public static List<String> getDungeonSchematicNames() {
        List<String> dungeonSchematicNamesArrayList = new ArrayList();
        for (String dungeonSchematicName : dungeonSchematicNames) {
            dungeonSchematicNamesArrayList.add(dungeonSchematicName);
        }
        return dungeonSchematicNamesArrayList;
    }
}
