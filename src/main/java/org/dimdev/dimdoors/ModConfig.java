package org.dimdev.dimdoors;

import java.util.LinkedHashSet;
import java.util.Set;

public final class ModConfig {
    public static ModConfig INSTANCE;
    public static final General GENERAL = new General();
    public static final Pockets POCKETS = new Pockets();
    public static final World WORLD = new World();
    public static final Dungeons DUNGEONS = new Dungeons();
    public static final Monoliths MONOLITHS = new Monoliths();
    public static final Limbo LIMBO = new Limbo();
    public static final Graphics GRAPHICS = new Graphics();

    private final General generalConfig;
    private final Pockets pocketsConfig;
    private final World worldConfig;
    private final Dungeons dungeonsConfig;
    private final Monoliths monolithsConfig;
    private final Limbo limboConfig;
    private final Graphics graphicsConfig;

    protected ModConfig(General generalConfig, Pockets pocketsConfig, World worldConfig, Dungeons dungeonsConfig, Monoliths monolithsConfig, Limbo limboConfig, Graphics graphicsConfig) {
        this.generalConfig = generalConfig;
        this.pocketsConfig = pocketsConfig;
        this.worldConfig = worldConfig;
        this.dungeonsConfig = dungeonsConfig;
        this.monolithsConfig = monolithsConfig;
        this.limboConfig = limboConfig;
        this.graphicsConfig = graphicsConfig;
    }

    public General getGeneralConfig() {
        return this.generalConfig;
    }

    public Pockets getPocketsConfig() {
        return this.pocketsConfig;
    }

    public World getWorldConfig() {
        return this.worldConfig;
    }

    public Dungeons getDungeonsConfig() {
        return this.dungeonsConfig;
    }

    public Monoliths getMonolithsConfig() {
        return this.monolithsConfig;
    }

    public Limbo getLimboConfig() {
        return this.limboConfig;
    }

    public Graphics getGraphicsConfig() {
        return this.graphicsConfig;
    }

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

        public static General create(boolean closeDoorBehind, double teleportOffset, boolean riftBoundingBoxInCreative, double riftCloseSpeed, double riftGrowthSpeed, int depthSpreadFactor, boolean useEnderPearlsInCrafting, double endermanSpawnChance, double endermanAggressiveChance) {
            General general = new General();
            general.closeDoorBehind = closeDoorBehind;
            general.teleportOffset = teleportOffset;
            general.riftBoundingBoxInCreative = riftBoundingBoxInCreative;
            general.riftCloseSpeed = riftCloseSpeed;
            general.riftGrowthSpeed = riftGrowthSpeed;
            general.depthSpreadFactor = depthSpreadFactor;
            general.useEnderPearlsInCrafting = useEnderPearlsInCrafting;
            general.endermanSpawnChance = endermanSpawnChance;
            general.endermanAggressiveChance = endermanAggressiveChance;
            return general;
        }
    }

    public static class Pockets {
        public int pocketGridSize = 32;
        public int maxPocketSize = 15;
        public int privatePocketSize = 2;
        public int publicPocketSize = 1;
        public boolean loadAllSchematics = false;
        public int cachedSchematics = 10;

        public static Pockets create(int pocketGridSize, int maxPocketSize, int privatePocketSize, int publicPocketSize, boolean loadAllSchematics, int cachedSchematics) {
            Pockets pockets = new Pockets();
            pockets.pocketGridSize = pocketGridSize;
            pockets.maxPocketSize = maxPocketSize;
            pockets.privatePocketSize = privatePocketSize;
            pockets.publicPocketSize = publicPocketSize;
            pockets.loadAllSchematics = loadAllSchematics;
            pockets.cachedSchematics = cachedSchematics;
            return pockets;
        }
    }

    public static class World {
        public double clusterGenChance = 0.0002;
        public int gatewayGenChance = 80;
        public Set<Integer> clusterDimBlacklist = new LinkedHashSet<>();
        public Set<Integer> gatewayDimBlacklist = new LinkedHashSet<>();

        public static World create(double clusterGenChance, int gatewayGenChance, Set<Integer> clusterDimBlacklist, Set<Integer> gatewayDimBlacklist) {
            World world = new World();
            world.clusterGenChance = clusterGenChance;
            world.gatewayGenChance = gatewayGenChance;
            world.clusterDimBlacklist = clusterDimBlacklist;
            world.gatewayDimBlacklist = gatewayDimBlacklist;
            return world;
        }
    }

    public static class Dungeons {
        public int maxDungeonDepth = 50;

        public static Dungeons create(int maxDungeonDepth) {
            Dungeons dungeons = new Dungeons();
            dungeons.maxDungeonDepth = maxDungeonDepth;
            return dungeons;
        }
    }

    public static class Monoliths {
        public boolean dangerousLimboMonoliths = false;
        public boolean monolithTeleportation = true;

        public static Monoliths create(boolean dangerousLimboMonoliths, boolean monolithTeleportation) {
            Monoliths monoliths = new Monoliths();
            monoliths.dangerousLimboMonoliths = dangerousLimboMonoliths;
            monoliths.monolithTeleportation = monolithTeleportation;
            return monoliths;
        }
    }

    public static class Limbo {
        public boolean universalLimbo = false;
        public boolean hardcoreLimbo = false;
        public double decaySpreadChance = 0.5;

        public static Limbo create(boolean universalLimbo, boolean hardcoreLimbo, double decaySpreadChance) {
            Limbo limbo = new Limbo();
            limbo.universalLimbo = universalLimbo;
            limbo.hardcoreLimbo = hardcoreLimbo;
            limbo.decaySpreadChance = decaySpreadChance;
            return limbo;
        }
    }

    public static class Graphics {
        public boolean showRiftCore = false;
        public int highlightRiftCoreFor = 15000;
        public double riftSize = 1;
        public double riftJitter = 1;

        public static Graphics create(boolean showRiftCore, int highlightRiftCoreFor, double riftSize, double riftJitter) {
            Graphics graphics = new Graphics();
            graphics.showRiftCore = showRiftCore;
            graphics.highlightRiftCoreFor = highlightRiftCoreFor;
            graphics.riftSize = riftSize;
            graphics.riftJitter = riftJitter;
            return graphics;
        }
    }

    static {
        INSTANCE = new ModConfig(
                new General(),
                new Pockets(),
                new World(),
                new Dungeons(),
                new Monoliths(),
                new Limbo(),
                new Graphics()
        );
    }
}
