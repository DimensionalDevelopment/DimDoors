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

import com.zixiken.dimdoors.shared.world.gateways.DimensionFilter;
import com.zixiken.dimdoors.shared.world.gateways.GatewayGenerator;
import lombok.Getter;
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
    @Getter private static int pocketGridSize = 32;
    @Getter private static int maxPocketSize = 15;
    @Getter private static int privatePocketSize = 3;
    @Getter private static int publicPocketSize = 2;
    @Getter private static int baseDimID = 684;
    private static String[] dungeonSchematicNames = {
        "default_dungeon_normal",
        "default_dungeon_nether"
    }; //@todo set default dungeon names
    @Getter private static int maxDungeonDepth = 100;
    @Getter private static int owCoordinateOffsetBase = 64;
    @Getter private static double owCoordinateOffsetPower = 1.3;
    @Getter private static int[] doorRelativeDepths = {-1, 0, 1};
    @Getter private static int[] doorRelativeDepthWeights = {20, 30, 50};

    @Getter private static boolean dangerousLimboMonolithsEnabled = false;
    @Getter private static boolean monolithTeleportationEnabled = true;

    @Getter private static int clusterGenerationChance;
    @Getter private static int gatewayGenerationChance;

    @Getter private static boolean limboEscapeEnabled;
    @Getter private static boolean universalLimboEnabled;

    @Getter private static DimensionFilter riftClusterDimensions;
    @Getter private static DimensionFilter riftGatewayDimensions;

    //Names of categories
    private static final String CATEGORY_WORLD_GENERATION = "world generation";

    private static int setConfigIntWithMaxAndMin(Configuration config, String category, String key, int defaultValue, String comment, int minValue, int maxValue) {
        Property prop = config.get(category, key, defaultValue, comment, minValue, maxValue);
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
        prop = config.get("monoliths", "dangerousLimboMonoliths", dangerousLimboMonolithsEnabled,
                "Are Monoliths in Limbo Dangerous? [default: false]");
        dangerousLimboMonolithsEnabled = prop.getBoolean();

        prop = config.get("monoliths", "monolithTeleportation", monolithTeleportationEnabled,
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
                "Sets how deep and wide any personal pocket can be. [min: 0, max: maxPocketSize, default: 3]", 0, maxPocketSize);

        publicPocketSize = setConfigIntWithMaxAndMin(config, "pocket_dimension", "publicPocketSize", publicPocketSize,
                "Sets how deep and wide any public pocket can be. [min: 0, max: maxPocketSize, default: 2]", 0, maxPocketSize);

        clusterGenerationChance = config.get(Configuration.CATEGORY_GENERAL, "Cluster Generation Chance", 2,
                "Sets the chance (out of " + GatewayGenerator.MAX_CLUSTER_GENERATION_CHANCE + ") that a cluster of rifts will " +
                        "generate in a given chunk. The default chance is 2.").getInt();

        gatewayGenerationChance = config.get(Configuration.CATEGORY_GENERAL, "Gateway Generation Chance", 15,
                "Sets the chance (out of " + GatewayGenerator.MAX_GATEWAY_GENERATION_CHANCE + ") that a Rift Gateway will " +
                        "generate in a given chunk. The default chance is 15.").getInt();

        //World Generation
        config.addCustomCategoryComment(CATEGORY_WORLD_GENERATION,
                "The following settings require lists of dimensions in a specific format. " +
                        "A list must consist of ranges separated by commas. A range may be a single number to indicate " +
                        "just one dimension or two numbers in the form \"X - Y\". Spaces are permitted " +
                        "but not required. Example: -100, -10 - -1, 20 - 30");

        riftClusterDimensions = loadFilter(config, "Rift Cluster", "Rift Clusters");
        riftGatewayDimensions = loadFilter(config, "Rift Gateway", "Rift Gateways");

        limboEscapeEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Limbo Escape", true,
                "Sets whether players are teleported out of Limbo when walking over the Eternal Fabric that " +
                        "generates near the bottom of the dimension. If disabled, players could still leave through " +
                        "dungeons in Limbo or by dying (if Hardcore Limbo is disabled). The default value is true.").getBoolean(true);

        universalLimboEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Universal Limbo", false,
                "Sets whether players are teleported to Limbo when they die in any dimension (except Limbo). " +
                        "Normally, players only go to Limbo if they die in a pocket dimension. This setting will not " +
                        "affect deaths in Limbo, which can be set with the Hardcore Limbo option. " +
                        "The default value is false.").getBoolean(false);

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

    private static DimensionFilter loadFilter(Configuration config, String prefix, String description) {
        boolean enableBlacklist = config.get(CATEGORY_WORLD_GENERATION, "Enable " + prefix + " Blacklist", true,
                "Sets whether " + description + " will not generate in certain blacklisted dimensions. " +
                        "If set to false, then " + description + " will follow a whitelist instead.").getBoolean(true);

        String whitelist = config.get(CATEGORY_WORLD_GENERATION, prefix + " Whitelist", "",
                "A list of the only dimensions in which " + description + " may generate.").getString();

        String blacklist = config.get(CATEGORY_WORLD_GENERATION, prefix + " Blacklist", "",
                "A list of dimensions in which " + description + " may not generate.").getString();

        try {
            if (enableBlacklist) {
                return DimensionFilter.parseBlacklist(blacklist);
            } else {
                return DimensionFilter.parseWhitelist(whitelist);
            }
        } catch (Exception inner) {
            throw new RuntimeException("An error occurred while loading a whitelist or blacklist setting for " +
                    description + ". Please make sure that your configuration file is set up correctly.", inner);
        }
    }
}
