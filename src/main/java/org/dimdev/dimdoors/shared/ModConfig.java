package org.dimdev.dimdoors.shared;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dimdev.dimdoors.DimDoors;

import static net.minecraftforge.common.config.Config.*;

@Config(modid = DimDoors.MODID, name = DimDoors.MODID + "/" + DimDoors.MODID, category="")
@Mod.EventBusSubscriber(modid = DimDoors.MODID)
public final class ModConfig {

    @Comment("General Config Options")
    public static General general = new General();
    @Comment({"Pocket Config Options",
                     "The following values determine the maximum sizes of different kinds of pockets. These values will only influence new worlds."})
    public static Pocket pocket = new Pocket();
    @Comment("World Generation Config Options")
    public static WorldGen world = new WorldGen();
    @Comment({"Dungeon Config Options",
                     "The following options will determine the depths, wandering offsets and contents of Dungeon Pockets."})
    public static Dungeons dungeon = new Dungeons();
    @Comment({"Monolith Config Options",
                     "How dangerous are Monoliths"})

    public static Monoliths monolith = new Monoliths();

    public static Limbo limbo = new Limbo();

    public static class General {
        @Name("Base Dimension ID")
        @Comment({"Dimension ID of the first Dimensional Doors dimension. Other dimensions will use consecutive IDs.",
                         "WARNING: If you change this after creating a world, you may lose these dimensions.",
                         "Default: 684"})
        public int baseDimensionID = 684;

        @Name("Status Bar Messages instead of Chat")
        @Comment({"This gives clients the options to either send messages",
                         "through the status bar (true) or through chat (false).",
                         "Default: true"})
        public boolean useStatusBar = true;

        @Name("Players Close Doors Behind Them")
        @Comment({"This options allows Dimensional Doors to automatically close the door once the player steps through.",
                         "Setting this to true automatically closes the doors, false allows doors to remain open once entered.",
                         "Default: true"})
        public boolean closeDoorBehind = true;
    }

    public static class Pocket {
        @Name("Pocket Grid Size")
        @Comment({"Sets how many chunks apart all pockets in pocket dimensions should be placed.",
                         "Default: 32 [Minimum = 4] [Maximum = 32]"})
        @RangeInt(min=4, max=32)
        public int pocketGridSize = 32;

        @Name("Max Pocket Size")
        @Comment({"Sets how large a pocket can be.",
                         "Default: 15 [Minimum = 0] [Maximum = Pocket Grid Size / 2"})
        public int maxPocketSize = 15;

        @Name("Private Pocket Size")
        @Comment({"Sets how large a personal pocket is when initially created.",
                         "Default: 2 [Minimum = 0] [Maximum = 7]"})
        @RangeInt(min = 0, max = 7)
        public int initialPrivatePocketSize = 2;

        @Name("Public Pocket Size")
        @Comment({"Sets how deep a public pocket created in the overworld will be on average.",
                         "Pockets created at a deeper depth will have larger sizes.",
                         "Default: 1 [Minimum = 0] [Maximum = Max Pocket Size]"})
        public int basePublicPocketSize = 1;

        @Name("Load All Schematics")
        @Comment({"Forces all available pocket schematics to load on game-start even if the configured maximum sizes mean that these",
                         "schematics will never be placed in any naturally generated pockets. This is meant for testing purposes,",
                         "because the /pocket command can be used to force generate these pockets.",
                         "Default: false"})
        public boolean loadAllSchematics = true;
    }

    public static class WorldGen {
        @Name("Rift Cluster Generation Chance")
        @Comment({"Sets the chance (out of 1.0) that a cluster of rifts will generate in a given chunk.",
                         "Default: 0.0002 [Minimum = 0] [Maximum = 1]"})
        @RangeDouble(min=0, max=1)
        public double clusterGenerationChance = 0.0002;

        @Name("Rift Cluster Dimension Type Blacklist")
        @Comment({"Dimension Type Blacklist for the generation of Rift Clusters. Add a dimension ID here to prevent",
                         "generation in these dimensions."})
        public int[] riftClusterDimensionTypeBlacklist = {};

        @Name("Gateway Generation Chance")
        @Comment({"Sets the chance (out of 1.0) that a Rift Gateway will generate in a given chunk.",
                         "Default: 0.0015 [Minimum = 0] [Maximum = 1]"})
        @RangeDouble(min=0, max=1)
        public double gatewayGenerationChance = 0.0015;

        @Name("Gateway Dimension Type Blacklist")
        @Comment({"Dimension Blacklist for the generation of Dimensional Gateways. Add a dimension ID here to prevent",
                         "generation in these dimensions."})
        public int[] gatewayDimensionTypeBlacklist = {};
    }

    public static class Dungeons {
        @Name("Maximum Dungeon Depth")
        @Comment("The depth at which limbo is located.")
        @RangeInt(min = 1)
        public int maxDungeonDepth = 100;
    }

    public static class Monoliths {
        @Name("Dangerous Monoliths")
        @Comment({"Are Monoliths in Limbo Dangerous?",
                         "Default: false"})
        public boolean dangerousLimboMonolithsEnabled = false;

        @Name("Monolith Teleportation")
        @Comment({"Is Monolith Teleportation enabled?",
                         "Default: true"})
        public boolean monolithTeleportationEnabled = true;
    }

    public static class Limbo {
        @Name("Universal Limbo")
        @Comment({"Sets whether players are teleported to Limbo when they die in any dimension (except Limbo).",
                "Normally, players only go to Limbo if they die in a pocket dimension. This setting will not",
                "affect deaths in Limbo, which can be set with the Hardcore Limbo option.",
                "Default: false"})
        public boolean universalLimboEnabled = false;

        @Name("Hardcore Limbo")
        @Comment({"Whether a player dying in limbo should respawn in limbo, making eternal fluid or gold dimensional doors",
                "the only way to get out",
                "Default: false"})
        public boolean hardcoreLimboEnabled = false;


    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DimDoors.MODID)) {
            ConfigManager.sync(event.getModID(), Type.INSTANCE); // Sync config values
        }
    }
}
