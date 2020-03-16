package org.dimdev.dimdoors;

import java.util.LinkedHashSet;
import java.util.Set;

public final class ModConfig {
    public static final General GENERAL = new General();
    public static final Pockets POCKETS = new Pockets();
    public static final World WORLD = new World();
    public static final Dungeons DUNGEONS = new Dungeons();
    public static final Monoliths MONOLITHS = new Monoliths();
    public static final Limbo LIMBO = new Limbo();
    public static final Graphics GRAPHICS = new Graphics();

    public static class General {
        public boolean closeDoorBehind = false;
        public double teleportOffset = 0.5;
        public boolean riftBoundingBoxInCreative;
        public double riftCloseSpeed = 0.005;
        public double riftGrowthSpeed = 1;
        public int depthSpreadFactor = 20;
        public boolean useEnderPearlsInCrafting = false;
        public double endermanSpawnChance = 0.001;
        public double endermanAggressiveChance = 0.5;
    }

    public static class Pockets {
        public int pocketGridSize = 32;
        public int maxPocketSize = 15;
        public int privatePocketSize = 2;
        public int publicPocketSize = 1;
        public boolean loadAllSchematics = false;
        public int cachedSchematics = 10;
    }

    public static class World {
        public double clusterGenChance = 0.0002;
        public double gatewayGenChance = 0.0015;
        public Set<Integer> clusterDimBlacklist = new LinkedHashSet<>();
        public Set<Integer> gatewayDimBlacklist = new LinkedHashSet<>();
    }

    public static class Dungeons {
        public int maxDungeonDepth = 50;
    }

    public static class Monoliths {
        public boolean dangerousLimboMonoliths = false;
        public boolean monolithTeleportation = true;
    }

    public static class Limbo {
        public boolean universalLimbo = false;
        public boolean hardcoreLimbo = false;
        public double decaySpreadChance = 0.5;
    }

    public static class Graphics {
        public boolean showRiftCore = false;
        public int highlightRiftCoreFor = 15000;
        public double riftSize = 1;
        public double riftJitter = 1;
    }
}
