package org.dimdev.dimdoors.shared;

import java.io.File;

import org.dimdev.dimdoors.shared.world.gateways.DimensionFilter;
import org.dimdev.dimdoors.shared.world.gateways.GatewayGenerator;
import lombok.Getter;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import scala.actors.threadpool.Arrays;

/**
 * @author Robijnvogel
 */
public final class Config {

    public static File configurationFolder;
    @Getter private static int pocketGridSize = 32;
    @Getter private static int maxPocketSize = 15;
    @Getter private static int privatePocketSize = 2;
    @Getter private static int publicPocketSize = 1;
    @Getter private static boolean loadAllSchematics = false;
    
    @Getter private static int baseDim = 684;
    
    // TODO: Remove these config options?
    @Getter private static int maxDungeonDepth = 100;
    @Getter private static int owCoordinateOffsetBase = 64;
    @Getter private static double owCoordinateOffsetPower = 1.3;
    @Getter private static int[] doorRelativeDepths = {-1, 0, 1};
    @Getter private static int[] doorRelativeDepthWeights = {20, 30, 50};

    @Getter private static boolean dangerousLimboMonolithsEnabled = false;
    @Getter private static boolean monolithTeleportationEnabled = true;

    @Getter private static int clusterGenerationChance = 2;
    @Getter private static int gatewayGenerationChance = 15;

    @Getter private static boolean limboEscapeEnabled = true;
    @Getter private static boolean universalLimboEnabled = false;

    // TODO: more complex functionality should be moved from the config class
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
        config.addCustomCategoryComment("general", "General configuration options.");
        Property prop = config.get("general", "baseDim", baseDim,
                "Dimension ID of the first Dimensional Doors dimension. Other dimensions will use consecutive IDs. NB: If you change this after creating a world, you may lose these dimensions. [default: "+baseDim+"]");
        baseDim = prop.getInt(baseDim);

        //Dungeons
        config.addCustomCategoryComment("dungeons", "The following options will determine the depths, wandering offsets and contents of Dungeon Pockets.");

        maxDungeonDepth = setConfigIntWithMaxAndMin(config, "dungeons", "maxDungeonDepth", maxDungeonDepth,
                "Sets the maximum (deepest) depth that a dungeon pocket can be at. [min: 1, max: 128, default: "+maxDungeonDepth+"]", 1, 128);

        owCoordinateOffsetBase = setConfigIntWithMaxAndMin(config, "dungeons", "owCoordinateOffsetBase", owCoordinateOffsetBase,
                "Determines how heavy the depth weighs when determining the overworld coordinates corresponding to a dungeon pocket. [min: 1, max: 128, default: "+owCoordinateOffsetBase+"]", 1, 128);

        prop = config.get("dungeons", "owCoordinateOffsetPower", owCoordinateOffsetPower,
                "Determines how heavy the depth weighs when determining the overworld coordinates corresponding to a dungeon pocket. [default: "+owCoordinateOffsetPower+"]"
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
                "Are Monoliths in Limbo Dangerous? [default: "+dangerousLimboMonolithsEnabled+"]");
        dangerousLimboMonolithsEnabled = prop.getBoolean();

        prop = config.get("monoliths", "monolithTeleportation", monolithTeleportationEnabled,
                "Is Monolith Teleportation enabled? [default: "+monolithTeleportationEnabled+"]");
        monolithTeleportationEnabled = prop.getBoolean();
        
        //Pocket Dimensions
        config.addCustomCategoryComment("pocket_dimension", "The following values determine the maximum sizes of different kinds of pockets. These values will only influence new worlds.");
        pocketGridSize = setConfigIntWithMaxAndMin(config, "pocket_dimension", "pocketGridSize", pocketGridSize,
                "Sets how many chunks apart all pockets in pocket dimensions should be placed. [min: 4, max: 32, default: "+pocketGridSize+"]", 4, 32);

        maxPocketSize = setConfigIntWithMaxAndMin(config, "pocket_dimension", "maxPocketSize", maxPocketSize,
                "Sets how deep and wide any pocket can be. [min: 0, max: pocketGridSize/2, default: "+maxPocketSize+"]", 0, (int) ((double) pocketGridSize / 2 - 0.5));

        privatePocketSize = setConfigIntWithMaxAndMin(config, "pocket_dimension", "privatePocketSize", privatePocketSize,
                "Sets how deep and wide any personal pocket can be. [min: 0, max: maxPocketSize, default: "+privatePocketSize+"]", 0, maxPocketSize);

        publicPocketSize = setConfigIntWithMaxAndMin(config, "pocket_dimension", "publicPocketSize", publicPocketSize,
                "Sets how deep and wide any public pocket can be. [min: 0, max: maxPocketSize, default: "+publicPocketSize+"]", 0, maxPocketSize);
        
        loadAllSchematics =config.get("pocket_dimension", "loadAllSchematics", loadAllSchematics,
                "Forces all available pocket schematics to load on game-start even if the configured maximum sizes mean that these " + 
                        "schematics will never be placed in any naturally generated pockets. This is meant for testing purposes, " +
                        "because the //pocket command can be used to force generate these pockets. " +
                        "The default value is "+loadAllSchematics+".").getBoolean(loadAllSchematics);

        clusterGenerationChance = config.get(CATEGORY_WORLD_GENERATION, "clusterGenerationChance", clusterGenerationChance,
                "Sets the chance (out of " + GatewayGenerator.MAX_CLUSTER_GENERATION_CHANCE + ") that a cluster of rifts will " +
                        "generate in a given chunk. The default chance is "+clusterGenerationChance+".").getInt(clusterGenerationChance);

        gatewayGenerationChance = config.get(CATEGORY_WORLD_GENERATION, "gatewayGenerationChance", gatewayGenerationChance,
                "Sets the chance (out of " + GatewayGenerator.MAX_GATEWAY_GENERATION_CHANCE + ") that a Rift Gateway will " +
                        "generate in a given chunk. The default chance is "+gatewayGenerationChance+".").getInt(gatewayGenerationChance);

        //World Generation
        config.addCustomCategoryComment(CATEGORY_WORLD_GENERATION,
                "The following settings require lists of dimension ranges in a specific format. "
                        + "A dimension range may be a single number to indicate just one dimension "
                        + "or two numbers in the form \"X - Y\". Spaces are permitted but not required. "
                        + "Examples: -1, 1, -1 - 1, -1-1. "
                        + "Separate dimension ranges need to be on a new line.");

        riftClusterDimensions = loadFilter(config, "cluster", "Rift Clusters");
        riftGatewayDimensions = loadFilter(config, "gateway", "Rift Gateways");

        limboEscapeEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Limbo Escape", limboEscapeEnabled,
                "Sets whether players are teleported out of Limbo when walking over the Eternal Fabric that " +
                        "generates near the bottom of the dimension. If disabled, players could still leave through " +
                        "dungeons in Limbo or by dying (if Hardcore Limbo is disabled). The default value is "+limboEscapeEnabled+".").getBoolean(limboEscapeEnabled);

        universalLimboEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Universal Limbo", universalLimboEnabled,
                "Sets whether players are teleported to Limbo when they die in any dimension (except Limbo). " +
                        "Normally, players only go to Limbo if they die in a pocket dimension. This setting will not " +
                        "affect deaths in Limbo, which can be set with the Hardcore Limbo option. " +
                        "The default value is "+universalLimboEnabled+".").getBoolean(universalLimboEnabled);

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

    private static DimensionFilter loadFilter(Configuration config, String prefix, String featureName) {
        //default values
        boolean enableBlacklist = true;
        String enableName = prefix + "Blacklist";
        String listName = prefix + "DimRangeList";
        String[] list = new String[0];
        
        enableBlacklist = config.get(CATEGORY_WORLD_GENERATION, enableName, enableBlacklist,
                "Sets whether " + listName + " is a blacklist or not." + "The default value is "+enableBlacklist+".").getBoolean(enableBlacklist);

        list = config.get(CATEGORY_WORLD_GENERATION, listName, list,
                "A list including/excluding the dimensions in which " + featureName + " may generate, depending on " + enableName + ".").getStringList();

        try {
            if (enableBlacklist) {
                return DimensionFilter.parseBlacklist(list);
            } else {
                return DimensionFilter.parseWhitelist(list);
            }
        } catch (Exception inner) {
            throw new RuntimeException("An error occurred while loading a whitelist or blacklist setting for " +
                    featureName + ". Please make sure that your configuration file is set up correctly.", inner);
        }
    }
}
