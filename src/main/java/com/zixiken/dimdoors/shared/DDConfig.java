/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 *
 * @author Robijnvogel
 */
public class DDConfig {

    public static File configurationFolder;
    private static int pocketGridSize = 8;
    private static int maxPocketSize = 4;
    private static int privatePocketSize = 3;
    private static int publicPocketSize = 2;
    private static int baseDimID = 684;
    private static String[] dungeonSchematicNames = {}; //@todo set default dungeon names

    private static int setConfigIntWithMaxAndMin(Configuration config, String category, String key, int defaultValue, String comment, int minValue, int maxValue) {
        Property prop = config.get(category, key, defaultValue,
                comment, minValue, maxValue);
        int value = prop.getInt(defaultValue);
        if (value < minValue) {
            value = minValue;
        } else if (value > maxValue) {
            value = maxValue;
        }
        prop.set(value);
        return value;
    }

    public static void loadConfig(FMLPreInitializationEvent event) {

        // Load config
        configurationFolder = new File(event.getModConfigurationDirectory(), "/DimDoors");
        if (!configurationFolder.exists()) {
            configurationFolder.mkdirs();
        }
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        // Setup general
        //@todo a comment in the config files about how these values only influence new worlds
        Property prop;
        pocketGridSize = setConfigIntWithMaxAndMin(config, Configuration.CATEGORY_GENERAL, "pocketGridSize", pocketGridSize,
                "Sets how many chunks apart all pockets in pocket dimensions should be placed. [min: 4, max: 16, default: 8]", 4, 16);
        DimDoors.log(DDConfig.class, "pocketGridSize was set to " + pocketGridSize);

        maxPocketSize = setConfigIntWithMaxAndMin(config, Configuration.CATEGORY_GENERAL, "maxPocketSize", maxPocketSize,
                "Sets how many deep and wide any pocket can be. [min: 1, max: pocketGridSize/2, default: 4]", 1, (int) (((double) pocketGridSize / 2) - 0.5));

        privatePocketSize = setConfigIntWithMaxAndMin(config, Configuration.CATEGORY_GENERAL, "privatePocketSize", privatePocketSize,
                "Sets how many deep and wide any personal pocket can be. [min: 1, max: maxPocketsSize, default: 3]", 1, maxPocketSize);

        publicPocketSize = setConfigIntWithMaxAndMin(config, Configuration.CATEGORY_GENERAL, "publicPocketSize", publicPocketSize,
                "Sets how many deep and wide any public pocket can be. [min: 1, max: maxPocketsSize, default: 2]", 1, maxPocketSize);

        prop = config.get(Configuration.CATEGORY_GENERAL, "dungeonSchematicNames", dungeonSchematicNames,
                "List of names of Pockets' jSon- and Schematic file names excluding extention. Custom json and schematic files can be dropped in the corresponding folders.");
        dungeonSchematicNames = prop.getStringList();

        prop = config.get(Configuration.CATEGORY_GENERAL, "baseDimID", baseDimID,
                "Dimension ID of the first Dimensional Doors pocket-containing dimension. Other pocket-containing dimensions will use consecutive IDs. [default: 684]");
        baseDimID = prop.getInt(baseDimID);

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
