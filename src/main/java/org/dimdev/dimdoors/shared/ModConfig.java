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

    public static class General {
        @Name("baseDimensionID")
        @Comment({"Dimension ID of the first Dimensional Doors dimension. Other dimensions will use consecutive IDs.",
                  "It is strongly recommendended not to change this value. Only change it if it conflicts with other mods."})
        @RequiresWorldRestart
        public int baseDimensionID = 684;

        @Name("useStatusBar")
        @Comment("When true, the status bar is used to relay status messages to the player, instead of the chat.")
        public boolean useStatusBar = true;

        @Name("closeDoorBehind")
        @Comment("When true, Dimensional Doors will automatically close when the player enters their portal.")
        public boolean closeDoorBehind = true;

        @Name("closeDoorBehind")
        @Comment("Distance in blocks to teleport the player in front of the dimensional door.")
        @RangeDouble(min = 0.5, max = 3)
        public double teleportOffset = 1;
    }

    public static class Pocket {
        @Name("A_pocketGridSize")
        @Comment("Sets how many chunks apart all pockets in any pocket dimensions should be placed.")
        @RangeInt(min = 4)
        public int pocketGridSize = 32;

        @Name("B_maxPocketSize")
        @Comment({"Sets the maximum size of any pocket. A 'maxPocketSize' of 'x' will allow for pockets up to (x + 1) * (x + 1) chunks.",
                  "If this is set to any value bigger than 'pocketGridSize / 2', the value of 'pocketGridSize / 2' will be used instead."})
        @RangeInt(min = 0)
        public int maxPocketSize = 15;

        @Name("C_privatePocketSize")
        @Comment({"Sets the minimum size of a newly created Private Pocket.",
                  "If this is set to any value bigger than 'maxPocketSize', the value of 'maxPocketSize' will be used instead."})
        @RangeInt(min = 0, max = 7)
        public int privatePocketSize = 2;

        @Name("D_publicPocketSize")
        @Comment({"Sets the minimum size of a newly created Public Pocket.",
                  "If this is set to any value bigger than 'privatePocketSize', the value of 'privatePocketSize' will be used instead."})
        @RangeInt(min = 0)
        public int publicPocketSize = 1;

        @Name("Z_loadAllSchematics")
        @Comment({"When true, all available Pocket Schematics will be loaded on game-start, even if the gridSize and pocketSize ",
                  "configuration fields would exclude these schematics from being used in 'naturally generated' pockets.",
                  "The /pocket command can be used to force-generate these pockets for dungeon building- or testing-purposes."})
        public boolean loadAllSchematics = false;
    }

    public static class World {
        @Name("A_clusterGenChance")
        @Comment("Sets the chance (out of 1.0) that a cluster of Rift Scars will generate in a given chunk.")
        @RangeDouble(min = 0.0, max = 1.0)
        public double clusterGenChance = 0.0002;

        @Name("A_gatewayGenChance")
        @Comment("Sets the chance (out of 1.0) that a Transient Portal gateway will generate in a given chunk.")
        @RangeDouble(min = 0.0, max = 1.0)
        public double gatewayGenChance = 0.0015;

        @Name("B_clusterDimBlacklist")
        @Comment({"Dimension Blacklist for the generation of Rift Scar clusters. Add a dimension ID here to prevent generation in certain dimensions.",
                  "Default: []"})
        public int[] clusterDimBlacklist = {};

        @Name("B_gatewayDimBlacklist")
        @Comment({"Dimension Blacklist for the generation of Transient Portal gateways. Add a dimension ID here to prevent generation in certain dimensions.",
                  "Default: []"})
        public int[] gatewayDimBlacklist = {};
    }

    public static class Dungeons {
        @Name("maxDungeonDepth")
        @Comment({"The depth at which limbo is located. If a Rift reaches any deeper than this while searching for a new ",
                  "destination, the player trying to enter the Rift will be sent straight to Limbo."})
        @RangeInt(min = 100)
        public int maxDungeonDepth = 2000;
    }

    public static class Monoliths {
        @Name("dangerousLimboMonoliths")
        @Comment("When true, Monoliths in Limbo attack the player and deal damage.")
        public boolean dangerousLimboMonoliths = false;

        @Name("monolithTeleportation")
        @Comment("When true, being exposed to the gaze of Monoliths for too long, will cause the player to be teleported to the void above Limbo.")
        public boolean monolithTeleportation = true;
    }

    public static class Limbo {
        @Name("universalLimbo")
        @Comment({"When true, players are also teleported to Limbo when they die in any non-Pocket Dimension (except Limbo itself).",
                "Otherwise, players only go to Limbo if they die in a Pocket Dimension."})
        public boolean universalLimbo = false;

        @Name("hardcoreLimbo")
        @Comment("When true, a player dying in Limbo will respawn in Limbo, making Eternal Fluid or Golden Dimensional Doors the only way to escape Limbo.")
        public boolean hardcoreLimbo = false;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DimDoors.MODID)) {
            ConfigManager.sync(event.getModID(), Type.INSTANCE); // Sync config values
        }
    }
}
