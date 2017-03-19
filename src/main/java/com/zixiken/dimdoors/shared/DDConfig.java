/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import scala.actors.threadpool.Arrays;

/**
 *
 * @author Robijnvogel
 */
public class DDConfig {

    public static final boolean HAVE_CONFIG_DEFAULTS_BEEN_CHECKED_FOR_CORRECTNESS = false; //@todo check this at each non-alpha release. This field does not have a use in the mod itself, but should ensure that the developers of this mod, don't forget to reset the config defaults to the right values before releasing a non-alpha release

    public static File configurationFolder;
    private static int pocketGridSize = 8;
    private static int maxPocketSize = 4;
    private static int privatePocketSize = 3;
    private static int publicPocketSize = 2;
    private static int baseDimID = 684;
    private static String[] dungeonSchematicNames = {}; //@todo set default dungeon names
    private static int maxDungeonDepth = 8;
    private static int owCoordinateOffsetBase = 64;
    private static double owCoordinateOffsetPower = 1.3;
    private static int[] doorRelativeDepths = new int[]{-1, 0, 1};
    private static int[] doorRelativeDepthWeights = new int[]{20, 30, 50};

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
                "Sets how deep and wide any pocket can be. [min: 1, max: pocketGridSize/2, default: 4]", 1, (int) (((double) pocketGridSize / 2) - 0.5));

        privatePocketSize = setConfigIntWithMaxAndMin(config, Configuration.CATEGORY_GENERAL, "privatePocketSize", privatePocketSize,
                "Sets how deep and wide any personal pocket can be. [min: 1, max: maxPocketsSize, default: 3]", 1, maxPocketSize);

        publicPocketSize = setConfigIntWithMaxAndMin(config, Configuration.CATEGORY_GENERAL, "publicPocketSize", publicPocketSize,
                "Sets how deep and wide any public pocket can be. [min: 1, max: maxPocketsSize, default: 2]", 1, maxPocketSize);

        prop = config.get(Configuration.CATEGORY_GENERAL, "dungeonSchematicNames", dungeonSchematicNames,
                "List of names of Pockets' jSon- and Schematic file names excluding extension. Custom json and schematic files can be dropped in the corresponding folders.");
        dungeonSchematicNames = prop.getStringList();

        prop = config.get(Configuration.CATEGORY_GENERAL, "baseDimID", baseDimID,
                "Dimension ID of the first Dimensional Doors dimension. Other dimensions will use consecutive IDs. NB: If you change this after creating a world, you may lose these dimensions. [default: 684]");
        baseDimID = prop.getInt(baseDimID);

        maxDungeonDepth = setConfigIntWithMaxAndMin(config, Configuration.CATEGORY_GENERAL, "maxDungeonDepth", maxDungeonDepth,
                "Sets the maximum (deepest) depth that a dungeon pocket can be at. [min: 1, max: 32, default: 8]", 1, 32);

        owCoordinateOffsetBase = setConfigIntWithMaxAndMin(config, Configuration.CATEGORY_GENERAL, "owCoordinateOffsetBase", owCoordinateOffsetBase,
                "Determines how heavy the depth weighs when determining the overworld coordinates corresponding to a dungeon pocket. [min: 1, max: 128, default: 64]", 1, 128);

        prop = config.get(Configuration.CATEGORY_GENERAL, "owCoordinateOffsetPower", owCoordinateOffsetPower,
                "Determines how heavy the depth weighs when determining the overworld coordinates corresponding to a dungeon pocket. [default: 1.3]"
                + System.getProperty("line.separator") + "max offset = (depth * owCoordinateOffsetBase)^owCoordinateOffsetPower");
        owCoordinateOffsetPower = prop.getDouble(owCoordinateOffsetPower);

        prop = config.get(Configuration.CATEGORY_GENERAL, "doorRelativeDepths", doorRelativeDepths,
                "List of possible depths that a new dungeon Pocket can generate at, relative to the origin door.");
        doorRelativeDepths = prop.getIntList();

        prop = config.get(Configuration.CATEGORY_GENERAL, "doorRelativeDepthWeights", doorRelativeDepthWeights,
                "List of weights (chances) of the relative depths in doorRelativeDepths. This list needs to have the same size.");
        doorRelativeDepthWeights = prop.getIntList();

        checkAndCorrectDoorRelativeDepths(config);

        // Save config
        config.save();
    }

    private static void checkAndCorrectDoorRelativeDepths(Configuration config) {
        int d = doorRelativeDepths.length;
        int dw = doorRelativeDepthWeights.length;
        if (d != dw) {
            Property prop;
            if (d > dw) {
                doorRelativeDepths = Arrays.copyOf(doorRelativeDepths, dw);
                prop = config.get(Configuration.CATEGORY_GENERAL, "doorRelativeDepths", doorRelativeDepths); //I hope that this works (not a disaster if it doesn't).
                prop.set(doorRelativeDepths);
            } else {
                doorRelativeDepthWeights = Arrays.copyOf(doorRelativeDepthWeights, d);
                prop = config.get(Configuration.CATEGORY_GENERAL, "doorRelativeDepthWeights", doorRelativeDepthWeights);
                prop.set(doorRelativeDepthWeights);
            }
        }
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

    public static int getBaseDimID() {
        return baseDimID;
    }

    /**
     * @return the owCoordinateOffsetBase
     */
    public static int getOwCoordinateOffsetBase() {
        return owCoordinateOffsetBase;
    }

    /**
     * @return the owCoordinateOffsetPower
     */
    public static double getOwCoordinateOffsetPower() {
        return owCoordinateOffsetPower;
    }

    /**
     * @return the doorRelativeDepths
     */
    public static int[] getDoorRelativeDepths() {
        return doorRelativeDepths;
    }

    /**
     * @return the doorRelativeDepthWeights
     */
    public static int[] getDoorRelativeDepthWeights() {
        return doorRelativeDepthWeights;
    }

    /**
     * @return the maxDungeonDepth
     */
    public static int getMaxDungeonDepth() {
        return maxDungeonDepth;
    }
}
