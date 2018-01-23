package org.dimdev.dimdoors.shared;

import lombok.Getter;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dimdev.dimdoors.DimDoors;

import net.minecraftforge.common.config.Config;

import java.io.File;

@Config(modid= DimDoors.MODID,name=DimDoors.MODID+"/"+DimDoors.MODID, category="")
@Mod.EventBusSubscriber(modid=DimDoors.MODID)
public class ModConfig {

    @Config.Comment({"General Config Options"})
    public static General general = new General();
    @Config.Comment({"Pocket Config Options",
                     "The following values determine the maximum sizes of different kinds of pockets. These values will only influence new worlds."})
    public static Pocket pocket = new Pocket();
    @Config.Comment({"World Generation Config Options"})
    public static WorldGen world = new WorldGen();
    @Config.Comment({"Dungeon Config Options",
                     "The following options will determine the depths, wandering offsets and contents of Dungeon Pockets."})
    public static Dungeons dungeon = new Dungeons();
    @Config.Comment({"Monolith Config Options",
                     "How dangerous are Monoliths"})
    public static Monoliths monolith = new Monoliths();

    public static class General {
        @Config.Name("Base Dimension ID")
        @Config.Comment({"Dimension ID of the first Dimensional Doors dimension. Other dimensions will use consecutive IDs.",
                         "WARNING: If you change this after creating a world, you may lose these dimensions.",
                         "Default: 684"})
        @Getter public int dimensionID = 684;

        @Config.Name("Status Bar Messages instead of Chat")
        @Config.Comment({"This gives clients the options to either send messages",
                         "through the status bar (true) or through chat (false).",
                         "Default: true"})
        @Getter public boolean actionMsg = true;

        @Config.Name("Doors Close Behind Players")
        @Config.Comment({"This options allows Dimensional Doors to automatically close the door once the player steps through.",
                         "Setting this to true automatically closes the doors, false allows doors to remain open once entered.",
                         "Default: true"})
        @Getter public boolean closeDoorBehind = true;
    }

    public static class Pocket {
        @Config.Name("Pocket Grid Size")
        @Config.Comment({"Sets how many chunks apart all pockets in pocket dimensions should be placed.",
                         "Default: 32 [Minimum = 4] [Maximum = 32]"})
        @Config.RangeInt(min=4, max=32)
        @Getter public int pocketGridSize = 32;

        @Config.Name("Max Pocket Size")
        @Config.Comment({"Sets how deep and wide any pocket can be.",
                         "Default: 15 [Minimum = 0] [Maximum = Pocket Grid Size / 2"})
        @Getter public int maxPocketSize = 15;

        @Config.Name("Private Pocket Size")
        @Config.Comment({"Sets how deep and wide any personal pocket can be.",
                         "Default: 2 [Minimum = 0] [Maximum = Max Pocket Size]"})
        @Getter public int privatePocketSize = 2;

        @Config.Name("Public Pocket Size")
        @Config.Comment({"Sets how deep and wide any public pocket can be.",
                         "Default: 1 [Minimum = 0] [Maximum = Max Pocket Size]"})
        @Getter public int publicPocketSize = 1;

        @Config.Name("Load All Schematics")
        @Config.Comment({"Forces all available pocket schematics to load on game-start even if the configured maximum sizes mean that these",
                         "schematics will never be placed in any naturally generated pockets. This is meant for testing purposes,",
                         "because the //pocket command can be used to force generate these pockets.",
                         "Default: false"})
        @Getter public boolean loadAllSchematics = false;
    }

    public static class WorldGen {
        @Config.Name("Rift Cluster Generation Chance")
        @Config.Comment({"Sets the chance (out of 1.0) that a cluster of rifts will generate in a given chunk.",
                         "Default: 0.0002 [Minimum = 0] [Maximum = 1]"})
        @Config.RangeDouble(min=0, max=1)
        @Getter public double clusterGenerationChance = 0.0002;

        @Config.Name("Rift Cluster Dimension Blacklist")
        @Config.Comment({"Dimension Blacklist for the generation of Rift Clusters. Add a dimension ID here to prevent",
                         "generation in these dimensions."})
        @Getter public int[] riftClusterDimensionBlacklist = {};

        @Config.Name("Gateway Generation Chance")
        @Config.Comment({"Sets the chance (out of 1.0) that a Rift Gateway will generate in a given chunk.",
                         "Default: 0.0015 [Minimum = 0] [Maximum = 1]"})
        @Config.RangeDouble(min=0, max=1)
        @Getter public double gatewayGenerationChance = 0.0015;

        @Config.Name("Gateway Dimension Blacklist")
        @Config.Comment({"Dimension Blacklist for the generation of Dimensional Gateways. Add a dimension ID here to prevent",
                         "generation in these dimensions."})
        @Getter public int[] gatewayDimensionBlacklist = {};
    }

    public static class Dungeons {
        @Config.Name("Maximum Dungeon Depth")
        @Config.Comment({""})
        @Config.RangeInt(min = 1, max = 128)
        @Getter public int maxDungeonDepth = 100;
    }

    public static class Monoliths {
        @Config.Name("Dangerous Monoliths")
        @Config.Comment({"Are Monoliths in Limbo Dangerous?",
                         "Default: false"})
        @Getter public boolean dangerousLimboMonolithsEnabled = false;

        @Config.Name("Monolith Teleportation")
        @Config.Comment({"Is Monolith Teleportation enabled?",
                         "Default: true"})
        @Getter public boolean monolithTeleportationEnabled = true;

        @Config.Name("Universal Limbo")
        @Config.Comment({"Sets whether players are teleported to Limbo when they die in any dimension (except Limbo).",
                         "Normally, players only go to Limbo if they die in a pocket dimension. This setting will not",
                         "affect deaths in Limbo, which can be set with the Hardcore Limbo option.",
                         "Default: false"})
        @Getter public boolean universalLimboEnabled = false;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DimDoors.MODID)) {
            ConfigManager.sync(event.getModID(), Config.Type.INSTANCE); // Sync config values
        }
    }

}
