package org.dimdev.dimdoors;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.client.util.Category;
import org.dimdev.dimdoors.client.util.Expose;
import org.dimdev.dimdoors.client.util.GetStrategy;
import org.dimdev.dimdoors.client.util.Getter;
import org.dimdev.dimdoors.client.util.IntSet;
import org.dimdev.dimdoors.client.util.RequiresRestart;
import org.dimdev.dimdoors.client.util.Title;
import org.dimdev.dimdoors.util.Codecs;
import org.dimdev.dimdoors.world.limbo.LimboDecay;

import net.fabricmc.loader.api.FabricLoader;

@Title("dimdoors.config.title")
@GetStrategy(suffix = "Config")
public final class ModConfig {
    public static ModConfig INSTANCE;
    private static final Path CONFIG_PATH;
    private static final Gson GSON;
    private static final Codec<ModConfig> CODEC;
    private static final String DEFAULT;
    private static final ModConfig FALLBACK;
    private static final Logger LOGGER;
    @Category
	@Getter
    private final General general;
	@Category
	@Getter
    private final Pockets pockets;
	@Category
	@Getter
    private final World world;
	@Category
	@Getter
    private final Dungeons dungeons;
	@Category
	@Getter
    private final Monoliths monoliths;
	@Category
	@Getter
    private final Limbo limbo;
	@Category
	@Getter
    private final Graphics graphics;

    private ModConfig(General general, Pockets pockets, World world, Dungeons dungeons, Monoliths monoliths, Limbo limbo, Graphics graphics) {
        this.general = general;
        this.pockets = pockets;
        this.world = world;
        this.dungeons = dungeons;
        this.monoliths = monoliths;
        this.limbo = limbo;
        this.graphics = graphics;
    }

    public General getGeneralConfig() {
        return this.general;
    }

    public Pockets getPocketsConfig() {
        return this.pockets;
    }

    public World getWorldConfig() {
        return this.world;
    }

    public Dungeons getDungeonsConfig() {
        return this.dungeons;
    }

    public Monoliths getMonolithsConfig() {
        return this.monoliths;
    }

    public Limbo getLimboConfig() {
        return this.limbo;
    }

    public Graphics getGraphicsConfig() {
        return this.graphics;
    }

    public static class General {
        public static final Codec<General> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.BOOL.fieldOf("closeDoorBehind").forGetter((general) -> general.closeDoorBehind),
                Codec.DOUBLE.fieldOf("teleportOffset").forGetter((general) -> general.teleportOffset),
                Codec.BOOL.fieldOf("riftBoundingBoxInCreative").forGetter((general) -> general.riftBoundingBoxInCreative),
                Codec.DOUBLE.fieldOf("riftCloseSpeed").forGetter((general) -> general.riftCloseSpeed),
                Codec.DOUBLE.fieldOf("riftGrowthSpeed").forGetter((general) -> general.riftGrowthSpeed),
                Codec.INT.fieldOf("depthSpreadFactor").forGetter((general) -> general.depthSpreadFactor),
                Codec.BOOL.fieldOf("useEnderPearlsInCrafting").forGetter((general) -> general.useEnderPearlsInCrafting),
                Codec.DOUBLE.fieldOf("endermanSpawnChance").forGetter((general) -> general.endermanSpawnChance),
                Codec.DOUBLE.fieldOf("endermanAggressiveChance").forGetter((general) -> general.endermanAggressiveChance)
        ).apply(instance, General::create));

        @Expose
        public boolean closeDoorBehind = false;
		@Expose
        public double teleportOffset = 0.5;
		@Expose
        public boolean riftBoundingBoxInCreative;
		@Expose
        public double riftCloseSpeed = 0.005;
		@Expose
        public double riftGrowthSpeed = 1;
		@Expose
        public int depthSpreadFactor = 20;
		@RequiresRestart
		@Expose
        public boolean useEnderPearlsInCrafting = false;
		@Expose
        public double endermanSpawnChance = 0.001;
		@Expose
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
        public static final Codec<Pockets> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.INT.fieldOf("pocketGridSize").forGetter((pockets) -> pockets.pocketGridSize),
                Codec.INT.fieldOf("maxPocketSize").forGetter((pockets) -> pockets.maxPocketSize),
                Codec.INT.fieldOf("privatePocketSize").forGetter((pockets) -> pockets.privatePocketSize),
                Codec.INT.fieldOf("publicPocketSize").forGetter((pockets) -> pockets.publicPocketSize),
                Codec.BOOL.fieldOf("loadAllSchematics").forGetter((pockets) -> pockets.loadAllSchematics),
                Codec.INT.fieldOf("cachedSchematics").forGetter((pockets) -> pockets.cachedSchematics)
        ).apply(instance, Pockets::create));
		@Expose
        public int pocketGridSize = 32;
		@Expose
        public int maxPocketSize = 15;
		@Expose
        public int privatePocketSize = 2;
		@Expose
        public int publicPocketSize = 1;
		@RequiresRestart
		@Expose
        public boolean loadAllSchematics = false;
		@RequiresRestart
		@Expose
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
        public static final Codec<World> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("clusterGenChance").forGetter((world) -> world.clusterGenChance),
                Codec.INT.fieldOf("gatewayGenChance").forGetter((world) -> world.gatewayGenChance),
                Codecs.INT_SET.fieldOf("clusterDimBlacklist").forGetter((world) -> world.clusterDimBlacklist),
                Codecs.INT_SET.fieldOf("gatewayDimBlacklist").forGetter((world) -> world.gatewayDimBlacklist)
        ).apply(instance, World::create));
        @RequiresRestart
		@Expose
        public double clusterGenChance = 0.0002;
        @RequiresRestart
		@Expose
        public int gatewayGenChance = 200;
        @IntSet
        @RequiresRestart
		@Expose
        public Set<Integer> clusterDimBlacklist = new LinkedHashSet<>();
        @IntSet
        @RequiresRestart
		@Expose
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
        public static final Codec<Dungeons> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("maxDungeonDepth").forGetter((dungeons) -> dungeons.maxDungeonDepth)
        ).apply(instance, Dungeons::create));
		@Expose
        public int maxDungeonDepth = 50;

        public static Dungeons create(int maxDungeonDepth) {
            Dungeons dungeons = new Dungeons();
            dungeons.maxDungeonDepth = maxDungeonDepth;
            return dungeons;
        }
    }

    public static class Monoliths {
        public static final Codec<Monoliths> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("dangerousLimboMonoliths").forGetter((monoliths) -> monoliths.dangerousLimboMonoliths),
                Codec.BOOL.fieldOf("monolithTeleportation").forGetter((monoliths) -> monoliths.monolithTeleportation)
        ).apply(instance, Monoliths::create));
		@Expose
        public boolean dangerousLimboMonoliths = false;
		@Expose
        public boolean monolithTeleportation = true;

        public static Monoliths create(boolean dangerousLimboMonoliths, boolean monolithTeleportation) {
            Monoliths monoliths = new Monoliths();
            monoliths.dangerousLimboMonoliths = dangerousLimboMonoliths;
            monoliths.monolithTeleportation = monolithTeleportation;
            return monoliths;
        }
    }

    public static class Limbo {
        public static final Codec<Limbo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("universalLimbo").forGetter((limbo) -> limbo.universalLimbo),
                Codec.BOOL.fieldOf("hardcoreLimbo").forGetter((limbo) -> limbo.hardcoreLimbo),
                Codec.DOUBLE.fieldOf("decaySpreadChance").forGetter((limbo) -> limbo.decaySpreadChance)
        ).apply(instance, Limbo::create));
		@Expose
        public boolean universalLimbo = false;
		@Expose
        public boolean hardcoreLimbo = false;
		@Expose
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
        public static final Codec<Graphics> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("showRiftCore").forGetter((graphics) -> graphics.showRiftCore),
                Codec.INT.fieldOf("highlightRiftCoreFor").forGetter((graphics) -> graphics.highlightRiftCoreFor),
                Codec.DOUBLE.fieldOf("riftSize").forGetter((graphics) -> graphics.riftSize),
                Codec.DOUBLE.fieldOf("riftJitter").forGetter((graphics) -> graphics.riftJitter)
        ).apply(instance, Graphics::create));
		@Expose
        public boolean showRiftCore = false;
		@Expose
        public int highlightRiftCoreFor = 15000;
		@Expose
        public double riftSize = 1;
		@Expose
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

    public static int deserialize() {
        LimboDecay.init();
        try {
            if (Files.isDirectory(CONFIG_PATH)) {
                Files.delete(CONFIG_PATH);
            }
            if (!Files.exists(CONFIG_PATH)) {
                Files.createFile(CONFIG_PATH);
                Files.write(CONFIG_PATH, DEFAULT.getBytes(StandardCharsets.UTF_8));
            }
            INSTANCE = CODEC.decode(
                    JsonOps.INSTANCE, new JsonParser().parse(
                            new InputStreamReader(
                                    Files.newInputStream(CONFIG_PATH)
                            )
                    ).getAsJsonObject()
            ).getOrThrow(false, System.err::println).getFirst();
            return 1;
        } catch (IOException e) {
            LOGGER.error("An Unexpected error occured when deserializing the Config. Using default values for now.");
            e.printStackTrace();
            INSTANCE = FALLBACK;
            return -1;
        }
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                General.CODEC.fieldOf("general").forGetter(ModConfig::getGeneralConfig),
                Pockets.CODEC.fieldOf("pockets").forGetter(ModConfig::getPocketsConfig),
                World.CODEC.fieldOf("world").forGetter(ModConfig::getWorldConfig),
                Dungeons.CODEC.fieldOf("dungeons").forGetter(ModConfig::getDungeonsConfig),
                Monoliths.CODEC.fieldOf("monoliths").forGetter(ModConfig::getMonolithsConfig),
                Limbo.CODEC.fieldOf("limbo").forGetter(ModConfig::getLimboConfig),
                Graphics.CODEC.fieldOf("graphics").forGetter(ModConfig::getGraphicsConfig)
        ).apply(instance, ModConfig::new));
        CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("dimensional_doors.json");
        GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
        FALLBACK = new ModConfig(
                new General(),
                new Pockets(),
                new World(),
                new Dungeons(),
                new Monoliths(),
                new Limbo(),
                new Graphics()
        );
        INSTANCE = FALLBACK;
        DEFAULT = GSON.toJson(CODEC.encodeStart(JsonOps.INSTANCE, INSTANCE).getOrThrow(false, System.err::println));
        LOGGER = LogManager.getLogger(ModConfig.class);
    }
}
