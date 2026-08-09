package org.dimdev.dimdoors;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class ModConfig {
    private static final String FILE_NAME = "dimdoors-config.json5";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(new TypeToken<ResourceKey<Level>>() {}.getType(), new LevelKeyAdapter())
            .create();

    private final General general = new General();
    private final Pockets pockets = new Pockets();
    private final World world = new World();
    private final Dungeons dungeons = new Dungeons();
    private final Monoliths monoliths = new Monoliths();
    private final Limbo limbo = new Limbo();
    private final Graphics graphics = new Graphics();
    private final Doors doors = new Doors();
    private final Decay decay = new Decay();

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

    public Doors getDoorsConfig() {
        return this.doors;
    }

    public Decay getDecayConfig() {
        return decay;
    }

    public static Path getConfigPath(Path configRoot) {
        return configRoot.resolve(FILE_NAME);
    }

    public static ModConfig load(Path configRoot) {
        Path configPath = getConfigPath(configRoot);
        if (!Files.exists(configPath)) {
            var config = new ModConfig();
            config.save(configRoot);

            return config;
        }

        try (var reader = Files.newBufferedReader(configPath)) {
            ModConfig config = GSON.fromJson(reader, ModConfig.class);
            return config == null ? new ModConfig() : config;
        } catch (IOException | JsonParseException e) {
            throw new IllegalStateException("Failed to load Dimensional Doors config from " + configPath, e);
        }
    }

    public void save(Path configRoot) {
        Path configPath = getConfigPath(configRoot);
        try {
            Files.createDirectories(configPath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                writer.write(GSON.toJson(this));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save Dimensional Doors config to " + configPath, e);
        }
    }

    public ModConfig copy() {
        return GSON.fromJson(GSON.toJson(this), ModConfig.class);
    }

    public static class General {
        public double teleportOffset = 0;
        public boolean riftBoundingBoxInCreative;
        public double riftCloseSpeed = 0.1;
        public double riftGrowthSpeed = 1;
        public boolean enableRiftDecay = true;
        public int depthSpreadFactor = 20;
        public double endermanSpawnChance = 0.00005;
        public double endermanAggressiveChance = 0.5;
        public boolean enableDebugMessages = false;
    }

    public static class Doors {
        public boolean closeDoorBehind = true;
        public DoorList doorList = new DoorList();
        public boolean placeRiftsInCreativeMode = true;

        public static class DoorList {
            public Mode mode = Mode.DISABLE;
            public Set<String> doors = new HashSet<>();

            public enum Mode {
                ENABLE("dimdoors.mode.enable"),
                DISABLE("dimdoors.mode.disable");

                private final String translationKey;

                Mode(String translationKey) {
                    this.translationKey = translationKey;
                }

                public @NotNull String getKey() {
                    return this.translationKey;
                }
            }
        }

        public boolean isAllowed(ResourceLocation id) {
            String idStr = id.toString();
            boolean contains = doorList.doors.contains(idStr);

            return (doorList.mode == DoorList.Mode.ENABLE) == contains;
        }
    }

    public static class Pockets {
        public int pocketGridSize = 32;
        public int maxPocketSize = 15;
        public int privatePocketSize = 2;
        public int publicPocketSize = 1;
        public int blocksColoredPerDye = 100;
    }

    public static class World {
        public double clusterGenChance = 20000;
        public List<String> clusterDimBlacklist = new LinkedList<>();
        public List<String> gatewayDimBlacklist = new LinkedList<>();
    }

    public static class Dungeons {
        public int maxDungeonDepth = 50;
    }

    public static class Monoliths {
        public boolean dangerousLimboMonoliths = false;
        public boolean monolithTeleportation = true;
    }

    public static class Limbo {
        public boolean genericDeathMessages = false;
        private final WorldList worldsLeadingToLimbo = new WorldList();
        public boolean hardcoreLimbo = false;

        public int limboReturnDistanceMax = 200;
        public int limboReturnDistanceMin = 100;

        public boolean decaySurroundings;

        public boolean tryPlayerBedSpawn = false;
        public boolean defaultToWorldSpawn = true;

        public float limboBlocksCorruptingExitWorldAmount = 5;
        @Nullable public ResourceKey<Level> escapeTargetWorld = Level.OVERWORLD;

        public boolean shouldUseLimbo(ResourceKey<Level> level) {
            return worldsLeadingToLimbo.blacklist != worldsLeadingToLimbo.list.contains(level);
        }

        public WorldList getWorldsLeadingToLimbo() {
            return worldsLeadingToLimbo;
        }

        public static final class WorldList {
            public List<ResourceKey<Level>> list;
            public boolean blacklist;

            public WorldList(List<ResourceKey<Level>> list, boolean blacklist) {
                this.list = list;
                this.blacklist = blacklist;
            }

            public WorldList() {
                this(new ArrayList<>(), false);
            }
        }
    }

    public static class Decay {
        public double decaySpreadChance = 1.0;
        public int decayDelay = 40;
        public boolean decaysIntoAir = true;
    }

    public static class Graphics {
        public boolean showRiftCore = false;
        public int highlightRiftCoreFor = 15000/1000;
        public double riftSize = 1;
        public double riftJitter = 1;
    }

    public static final class LevelKeyAdapter implements JsonSerializer<ResourceKey<Level>>, JsonDeserializer<ResourceKey<Level>> {

        @Override
        public ResourceKey<Level> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(json.getAsJsonPrimitive().getAsString()));
        }

        @Override
        public JsonElement serialize(ResourceKey<Level> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.location().toString());
        }
    }
}
