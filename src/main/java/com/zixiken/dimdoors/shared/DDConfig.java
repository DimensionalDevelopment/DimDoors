/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    private static int pocketGridSize = 32;
    private static int maxPocketSize = 15;
    private static int privatePocketSize = 3;
    private static int publicPocketSize = 2;
    private static int baseDimID = 684;
    private static String[] dungeonSchematicNames = {
        "default_dungeon_normal",
        "default_dungeon_nether"
    }; //@todo set default dungeon names
    private static int maxDungeonDepth = 8;
    private static int owCoordinateOffsetBase = 64;
    private static double owCoordinateOffsetPower = 1.3;
    private static int[] doorRelativeDepths = {-1, 0, 1};
    private static int[] doorRelativeDepthWeights = {20, 30, 50};

    private static boolean dangerousLimboMonolithsEnabled = false;
    private static boolean monolithTeleportationEnabled = true;

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
        config.addCustomCategoryComment("aa_general", "General configuration options.");
        Property prop = config.get("aa_general", "baseDimID", baseDimID,
                "Dimension ID of the first Dimensional Doors dimension. Other dimensions will use consecutive IDs. NB: If you change this after creating a world, you may lose these dimensions. [default: 684]");
        baseDimID = prop.getInt(baseDimID);

        //Dungeons
        config.addCustomCategoryComment("dungeons", "The following options will determine the depths, wandering offsets and contents of Dungeon Pockets.");
        prop = config.get("dungeons", "dungeonSchematicNames", dungeonSchematicNames,
                "List of names of Dungeon Pockets' jSon- file names excluding extension. Custom json and schematic files can be dropped in the corresponding config folders.");
        dungeonSchematicNames = prop.getStringList();

        maxDungeonDepth = setConfigIntWithMaxAndMin(config, "dungeons", "maxDungeonDepth", maxDungeonDepth,
                "Sets the maximum (deepest) depth that a dungeon pocket can be at. [min: 1, max: 32, default: 8]", 1, 32);

        owCoordinateOffsetBase = setConfigIntWithMaxAndMin(config, "dungeons", "owCoordinateOffsetBase", owCoordinateOffsetBase,
                "Determines how heavy the depth weighs when determining the overworld coordinates corresponding to a dungeon pocket. [min: 1, max: 128, default: 64]", 1, 128);

        prop = config.get("dungeons", "owCoordinateOffsetPower", owCoordinateOffsetPower,
                "Determines how heavy the depth weighs when determining the overworld coordinates corresponding to a dungeon pocket. [default: 1.3]"
                + System.getProperty("line.separator") + "max offset = (depth * owCoordinateOffsetBase)^owCoordinateOffsetPower");
        owCoordinateOffsetPower = prop.getDouble(owCoordinateOffsetPower);

        prop = config.get("dungeons", "doorRelativeDepths", doorRelativeDepths,
                "List of possible depths that a new dungeon Pocket can generate at, relative to the origin door.");
        doorRelativeDepths = prop.getIntList();

        prop = config.get("dungeons", "doorRelativeDepthWeights", doorRelativeDepthWeights,
                "List of weights (chances) of the relative depths in doorRelativeDepths. This list needs to have the same size as doorRelativeDepths.");
        doorRelativeDepthWeights = prop.getIntList();
        checkAndCorrectDoorRelativeDepths(config);

        //Monoliths
        config.addCustomCategoryComment("monoliths", "How dangerous are Monoliths");
        prop = config.get("monoliths", "dangerousLimboMonolithsDisabled", dangerousLimboMonolithsEnabled,
                "Are Monoliths in Limbo Dangerous? [default: false]");
        dangerousLimboMonolithsEnabled = prop.getBoolean();

        prop = config.get("monoliths", "monolithTeleportationEnabled", monolithTeleportationEnabled,
                "Is Monolith Teleportation enabled? [default: true]");
        monolithTeleportationEnabled = prop.getBoolean();
        
        //Pocket_Dimensions
        config.addCustomCategoryComment("pocket_dimension", "The following values determine the maximum sizes of different kinds of pockets. These values will only influence new worlds.");
        pocketGridSize = setConfigIntWithMaxAndMin(config, "pocket_dimension", "pocketGridSize", pocketGridSize,
                "Sets how many chunks apart all pockets in pocket dimensions should be placed. [min: 4, max: 32, default: 32]", 4, 32);
        DimDoors.log(DDConfig.class, "pocketGridSize was set to " + pocketGridSize);

        maxPocketSize = setConfigIntWithMaxAndMin(config, "pocket_dimension", "maxPocketSize", maxPocketSize,
                "Sets how deep and wide any pocket can be. [min: 0, max: pocketGridSize/2, default: 4]", 0, (int) ((double) pocketGridSize / 2 - 0.5));

        privatePocketSize = setConfigIntWithMaxAndMin(config, "pocket_dimension", "privatePocketSize", privatePocketSize,
                "Sets how deep and wide any personal pocket can be. [min: 0, max: maxPocketsSize, default: 3]", 0, maxPocketSize);

        publicPocketSize = setConfigIntWithMaxAndMin(config, "pocket_dimension", "publicPocketSize", publicPocketSize,
                "Sets how deep and wide any public pocket can be. [min: 0, max: maxPocketsSize, default: 2]", 0, maxPocketSize);

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
        List<String> dungeonSchematicNamesArrayList = new ArrayList<>();
        Collections.addAll(dungeonSchematicNamesArrayList, dungeonSchematicNames);
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

    public static boolean isDangerousLimboMonolithsDisabled() {
        return dangerousLimboMonolithsEnabled;
    }

    public static boolean isMonolithTeleportationEnabled() {
        return monolithTeleportationEnabled;
    }
}
