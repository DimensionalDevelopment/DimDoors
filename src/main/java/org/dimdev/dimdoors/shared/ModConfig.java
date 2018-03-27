package org.dimdev.dimdoors.shared;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dimdev.dimdoors.DimDoors;

import static net.minecraftforge.common.config.Config.*;

@Config(modid = DimDoors.MODID, name = DimDoors.MODID, category = "")
@Mod.EventBusSubscriber(modid = DimDoors.MODID)
public final class ModConfig { // TODO: localize the rest

    public static General general = new General();
    public static Pocket pockets = new Pocket();
    public static World world = new World();
    public static Dungeons dungeons = new Dungeons();
    public static Monoliths monoliths = new Monoliths();
    public static Limbo limbo = new Limbo();

    private final static int BASE_DIMENSION_ID_DEF = 684;
    private final static boolean USE_STATUS_BAR_DEF = true;
    private final static boolean CLOSE_DOOR_BEHIND_DEF = true;

    public static class General {
        @Name("baseDimensionID")        
        @Comment({"Dimension ID of the first Dimensional Doors dimension. Other dimensions will use consecutive IDs.",
            "It is strongly recommendended not to change this value. Only change it if it conflicts with other mods.",
            "Default: " + BASE_DIMENSION_ID_DEF})
        @RequiresWorldRestart
        public int baseDimensionID = BASE_DIMENSION_ID_DEF;

        @Name("useStatusBar")        
        @Comment({"When true, the status bar is used to relay status messages to the player, instead of the chat.",
            "Default: " + USE_STATUS_BAR_DEF})
        public boolean useStatusBar = USE_STATUS_BAR_DEF;

        @Name("closeDoorBehind")        
        @Comment({"When true, Dimensional Doors will automatically close when the player enters their portal.",
            "Default: " + CLOSE_DOOR_BEHIND_DEF})
        public boolean closeDoorBehind = CLOSE_DOOR_BEHIND_DEF;

        @Name("closeDoorBehind")
        @Comment("Distance in blocks to teleport the player in front of the dimensional door.")
        @RangeDouble(min = 0.5, max = 3)
        public double teleportOffset = 1;
    }

    private final static int POCKET_GRID_SIZE_MIN = 4;
    private final static int POCKET_GRID_SIZE_DEF = 32;
    private final static int MAX_POCKET_SIZE_MIN = 0;
    private final static int MAX_POCKET_SIZE_DEF = 15;
    private final static int PRIVATE_POCKET_SIZE_MIN = 0;
    private final static int PRIVATE_POCKET_SIZE_MAX = 7;
    private final static int PRIVATE_POCKET_SIZE_DEF = 2;
    private final static int PUBLIC_POCKET_SIZE_MIN = 0;
    private final static int PUBLIC_POCKET_SIZE_DEF = 1;
    private final static boolean LOAD_ALL_SCHEMATICS_DEF = false;

    public static class Pocket {
        @Name("A_pocketGridSize")
        @Comment({"Sets how many chunks apart all pockets in any pocket dimensions should be placed.",
            "Default: " + POCKET_GRID_SIZE_DEF})
        @RangeInt(min = POCKET_GRID_SIZE_MIN)
        public int pocketGridSize = POCKET_GRID_SIZE_DEF;

        @Name("B_maxPocketSize")
        @Comment({"Sets the maximum size of any pocket. A 'maxPocketSize' of 'x' will allow for pockets up to (x + 1) * (x + 1) chunks.",
            "If this is set to any value bigger than 'pocketGridSize / 2', the value of 'pocketGridSize / 2' will be used instead.",
            "Default: " + MAX_POCKET_SIZE_DEF})
        @RangeInt(min = MAX_POCKET_SIZE_MIN)
        public int maxPocketSize = MAX_POCKET_SIZE_DEF;

        @Name("C_privatePocketSize")
        @Comment({"Sets the minimum size of a newly created Private Pocket.",
            "If this is set to any value bigger than 'maxPocketSize', the value of 'maxPocketSize' will be used instead.",
            "Default: " + PRIVATE_POCKET_SIZE_DEF})
        @RangeInt(min = PRIVATE_POCKET_SIZE_MIN, max = PRIVATE_POCKET_SIZE_MAX)
        public int privatePocketSize = PRIVATE_POCKET_SIZE_DEF;

        @Name("D_publicPocketSize")        
        @Comment({"Sets the minimum size of a newly created Public Pocket.",
            "If this is set to any value bigger than 'privatePocketSize', the value of 'privatePocketSize' will be used instead.",
            "Default: " + PUBLIC_POCKET_SIZE_DEF})
        @RangeInt(min = PUBLIC_POCKET_SIZE_MIN)
        public int publicPocketSize = PUBLIC_POCKET_SIZE_DEF;

        @Name("Z_loadAllSchematics")        
        @Comment({"When true, all available Pocket Schematics will be loaded on game-start, even if the gridSize and pocketSize ",
            "configuration fields would exclude these schematics from being used in 'naturally generated' pockets.",
            "The /pocket command can be used to force-generate these pockets for dungeon building- or testing-purposes.",
            "Default: " + LOAD_ALL_SCHEMATICS_DEF})
        public boolean loadAllSchematics = LOAD_ALL_SCHEMATICS_DEF;
    }
    
    private final static double CLUSTER_GEN_CHANCE_MIN = 0.0;
    private final static double CLUSTER_GEN_CHANCE_MAX = 1.0;
    private final static double CLUSTER_GEN_CHANCE_DEF = 0.0002;
    private final static double GATEWAY_GEN_CHANCE_MIN = 0.0;
    private final static double GATEWAY_GEN_CHANCE_MAX = 1.0;
    private final static double GATEWAY_GEN_CHANCE_DEF = 0.0015;

    public static class World {
        @Name("A_clusterGenChance")
        @Comment({"Sets the chance (out of 1.0) that a cluster of Rift Scars will generate in a given chunk.",
            "Default: " + CLUSTER_GEN_CHANCE_DEF})
        @RangeDouble(min = CLUSTER_GEN_CHANCE_MIN, max = CLUSTER_GEN_CHANCE_MAX)
        public double clusterGenChance = CLUSTER_GEN_CHANCE_DEF;

        @Name("A_gatewayGenChance")
        @Comment({"Sets the chance (out of 1.0) that a Transient Portal gateway will generate in a given chunk.",
            "Default: " + GATEWAY_GEN_CHANCE_DEF})
        @RangeDouble(min = GATEWAY_GEN_CHANCE_MIN, max = GATEWAY_GEN_CHANCE_MAX)
        public double gatewayGenChance = GATEWAY_GEN_CHANCE_DEF;

        @Name("B_clusterDimBlacklist")
        @Comment({"Dimension Blacklist for the generation of Rift Scar clusters. Add a dimension ID here to prevent generation in certain dimensions.",
            "Default: []"})
        public int[] clusterDimBlacklist = {};

        @Name("B_gatewayDimBlacklist")
        @Comment({"Dimension Blacklist for the generation of Transient Portal gateways. Add a dimension ID here to prevent generation in certain dimensions.",
            "Default: []"})
        public int[] gatewayDimBlacklist = {};
    }

    private final static int MAX_DUNGEON_DEPTH_DEF = 2000;
    private final static int MAX_DUNGEON_DEPTH_MIN = 100;

    public static class Dungeons {
        @Name("maxDungeonDepth")
        @Comment({"The depth at which limbo is located. If a Rift reaches any deeper than this while searching for a new ",
            "destination, the player trying to enter the Rift will be sent straight to Limbo.",
            "Default: " + MAX_DUNGEON_DEPTH_DEF})
        @RangeInt(min = MAX_DUNGEON_DEPTH_MIN)
        public int maxDungeonDepth = MAX_DUNGEON_DEPTH_DEF;
    }
    
    private final static boolean DANGEROUS_LIMBO_MONOLITHS_DEF = false;
    private final static boolean MONOLITH_TELEPORTATION_DEF = true;

    public static class Monoliths {
        @Name("dangerousLimboMonoliths")
        @Comment({"When true, Monoliths in Limbo attack the player and deal damage.",
            "Default: " + DANGEROUS_LIMBO_MONOLITHS_DEF})
        public boolean dangerousLimboMonoliths = DANGEROUS_LIMBO_MONOLITHS_DEF;

        @Name("monolithTeleportation")
        @Comment({"When true, being exposed to the gaze of Monoliths for too long, will cause the player to be teleported to the void above Limbo.",
            "Default: " + MONOLITH_TELEPORTATION_DEF})
        public boolean monolithTeleportation = MONOLITH_TELEPORTATION_DEF;
    }
    
    private final static boolean UNIVERSAL_LIMBO_DEF = false;
    private final static boolean HARDCORE_LIMBO_DEF = false;

    public static class Limbo {
        @Name("universalLimbo")        
        @Comment({"When true, players are also teleported to Limbo when they die in any non-Pocket Dimension (except Limbo itself).",
            "Otherwise, players only go to Limbo if they die in a Pocket Dimension.",
            "Default: " + UNIVERSAL_LIMBO_DEF})
        public boolean universalLimbo = UNIVERSAL_LIMBO_DEF;

        @Name("hardcoreLimbo")        
        @Comment({"When true, a player dying in Limbo will respawn in Limbo, making Eternal Fluid or Golden Dimensional Doors the only way to escape Limbo.",
            "Default: " + HARDCORE_LIMBO_DEF})
        public boolean hardcoreLimbo = HARDCORE_LIMBO_DEF;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DimDoors.MODID)) {
            ConfigManager.sync(event.getModID(), Type.INSTANCE); // Sync config values
        }
    }
}
