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
public final class ModConfig {

    public static General general = new General();
    public static Pockets pockets = new Pockets();
    public static World world = new World();
    public static Dungeons dungeons = new Dungeons();
    public static Monoliths monoliths = new Monoliths();
    public static Limbo limbo = new Limbo();
    public static Graphics graphics = new Graphics();

    public static class General {
        @Name("baseDimensionID")
        @LangKey("dimdoors.general.baseDimensionID")
        @RequiresWorldRestart
        public int baseDimensionID = 684;

        @Name("useStatusBar")
        @LangKey("dimdoors.general.useStatusBar")
        public boolean useStatusBar = true;

        @Name("closeDoorBehind")
        @LangKey("dimdoors.general.closeDoorBehind")
        public boolean closeDoorBehind = true;

        @Name("teleportOffset")
        @LangKey("dimdoors.general.teleportOffset")
        @RangeDouble(min = 0, max = 3)
        public double teleportOffset = 0.5;

        @Name("riftBoundingBoxInCreative")
        @LangKey("dimdoors.general.riftBoundingBoxInCreative")
        public boolean riftBoundingBoxInCreative;

        @Name("riftCloseSpeed")
        @LangKey("dimdoors.general.riftCloseSpeed")
        @RangeDouble(min = 0)
        public float riftCloseSpeed = 1f;
    }

    public static class Pockets {
        @Name("pocketGridSize")
        @LangKey("dimdoors.pockets.pocketGridSize")
        @RangeInt(min = 4)
        public int pocketGridSize = 32;

        @Name("maxPocketSize")
        @LangKey("dimdoors.pockets.maxPocketSize")
        @RangeInt(min = 0)
        public int maxPocketSize = 15;

        @Name("privatePocketSize")
        @LangKey("dimdoors.pockets.privatePocketSize")
        @RangeInt(min = 0, max = 7)
        public int privatePocketSize = 2;

        @Name("publicPocketSize")
        @LangKey("dimdoors.pockets.publicPocketSize")
        @RangeInt(min = 0)
        public int publicPocketSize = 1;

        @Name("loadAllSchematics")
        @LangKey("dimdoors.pockets.loadAllSchematics")
        public boolean loadAllSchematics = false;
    }

    public static class World {
        @Name("clusterGenChance")
        @LangKey("dimdoors.world.clusterGenChance")
        @RangeDouble(min = 0.0, max = 1.0)
        public double clusterGenChance = 0.0002;

        @Name("gatewayGenChance")
        @LangKey("dimdoors.world.gatewayGenChance")
        @RangeDouble(min = 0.0, max = 1.0)
        public double gatewayGenChance = 0.0015;

        @Name("clusterDimBlacklist")
        @LangKey("dimdoors.world.clusterDimBlacklist")
        public int[] clusterDimBlacklist = {};

        @Name("gatewayDimBlacklist")
        @LangKey("dimdoors.world.gatewayDimBlacklist")
        public int[] gatewayDimBlacklist = {};
    }

    public static class Dungeons {
        @Name("maxDungeonDepth")
        @LangKey("dimdoors.dungeons.maxDungeonDepth")
        @RangeInt(min = 5)
        public int maxDungeonDepth = 50;
    }

    public static class Monoliths {
        @Name("dangerousLimboMonoliths")
        @LangKey("dimdoors.monoliths.dangerousLimboMonoliths")
        public boolean dangerousLimboMonoliths = false;

        @Name("monolithTeleportation")
        @LangKey("dimdoors.monoliths.monolithTeleportation")
        public boolean monolithTeleportation = true;
    }

    public static class Limbo {
        @Name("universalLimbo")
        @LangKey("dimdoors.limbo.universalLimbo")
        public boolean universalLimbo = false;

        @Name("hardcoreLimbo")
        @LangKey("dimdoors.limbo.hardcoreLimbo")
        public boolean hardcoreLimbo = false;
    }

    public static class Graphics {
        @Name("showRiftCore")
        @LangKey("dimdoors.graphics.showRiftCore")
        public boolean showRiftCore = false;

        @Name("highlightRiftCoreFor")
        @LangKey("dimdoors.graphics.highlightRiftCoreFor")
        @RangeInt(min = -1)
        public int highlightRiftCoreFor = 15000;
    }


    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DimDoors.MODID)) {
            ConfigManager.sync(event.getModID(), Type.INSTANCE); // Sync config values
        }
    }
}
