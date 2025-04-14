package org.dimdev.dimdoors;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
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
import java.util.*;

import static me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import static me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.*;

@SuppressWarnings("FieldMayBeFinal")
@Config(name = "dimdoors")
public final class ModConfig implements ConfigData {
	@TransitiveObject
	@Category("general")
	private General general = new General();
	@TransitiveObject
	@Category("pockets")
	private Pockets pockets = new Pockets();
	@TransitiveObject
	@Category("world")
	private World world = new World();
	@TransitiveObject
	@Category("dungeons")
	private Dungeons dungeons = new Dungeons();
	@TransitiveObject
	@Category("monoliths")
	private Monoliths monoliths = new Monoliths();
	@TransitiveObject
	@Category("limbo")
	private Limbo limbo = new Limbo();
	@TransitiveObject
	@Category("graphics")
	private Graphics graphics = new Graphics();
	@TransitiveObject
	@Category("doors")
	private Doors doors = new Doors();

	@TransitiveObject
	@Category("decay")
	private Decay decay = new Decay();

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

	public static class General {
		@Tooltip public double teleportOffset = 0;
		@Tooltip public boolean riftBoundingBoxInCreative;
		@Tooltip public double riftCloseSpeed = 0.1;
		@Tooltip public double riftGrowthSpeed = 1;
		@Tooltip public boolean enableRiftDecay = true;
		@Tooltip public int depthSpreadFactor = 20;
		@Tooltip public double endermanSpawnChance = 0.00005;
		@Tooltip public double endermanAggressiveChance = 0.5;
        @Tooltip public boolean enableDebugMessages = false;
    }

	public static class Doors {
		@Tooltip public boolean closeDoorBehind = true;
		@Tooltip @CollapsibleObject public DoorList doorList = new DoorList();
		@Tooltip public boolean placeRiftsInCreativeMode = true;

		public static class DoorList {
			@Tooltip public Mode mode = Mode.DISABLE;
			@Tooltip public Set<String> doors = new HashSet<>();

			@EnvironmentInterface(value = EnvType.CLIENT, itf = SelectionListEntry.Translatable.class)
			public enum Mode implements SelectionListEntry.Translatable {
				ENABLE("dimdoors.mode.enable"),
				DISABLE("dimdoors.mode.disable");

				private String translationKey;

				Mode(String translationKey) {
					this.translationKey = translationKey;
				}

				@Override
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
		@Tooltip public int pocketGridSize = 32;
		@Tooltip public int maxPocketSize = 15;
		@Tooltip public int privatePocketSize = 2;
		@Tooltip public int publicPocketSize = 1;
		@Tooltip public String defaultWeightEquation = "5";
		@Tooltip public int fallbackWeight = 5;
//		@Tooltip @EnumHandler(option = BUTTON) public ExtendedResourcePackActivationType classicPocketsResourcePackActivationType = ExtendedResourcePackActivationType.DEFAULT_ENABLED;
//		@Tooltip @EnumHandler(option = BUTTON) public ExtendedResourcePackActivationType defaultPocketsResourcePackActivationType = ExtendedResourcePackActivationType.DEFAULT_ENABLED;
		@Tooltip public boolean asyncWorldEditPocketLoading = true;
		@Tooltip public boolean canUseRiftSignatureInPrivatePockets = true;
	}

	public static class World {
		@RequiresRestart
		@Tooltip public double clusterGenChance = 20000;
		@RequiresRestart
		@Tooltip public List<String> clusterDimBlacklist = new LinkedList<>();
		@RequiresRestart
		@Tooltip public List<String> gatewayDimBlacklist = new LinkedList<>();
	}

	public static class Dungeons {
		@Tooltip public int maxDungeonDepth = 50;
	}

	public static class Monoliths {
		@Tooltip public boolean dangerousLimboMonoliths = false;
		@Tooltip public boolean monolithTeleportation = true;
	}

	public static class Limbo {
		@CollapsibleObject
		@RequiresRestart
		@Tooltip private WorldList worldsLeadingToLimbo = new WorldList();
		@Tooltip public boolean hardcoreLimbo = false;

		@Tooltip public int limboReturnDistanceMax = 5000;
		@Tooltip public int limboReturnDistanceMin = 0;

		@Tooltip public boolean decaySurroundings = true;

		@Tooltip public boolean tryPlayerBedSpawn = false;
		@Tooltip public boolean defaultToWorldSpawn = true;


		@Tooltip public float limboBlocksCorruptingExitWorldAmount = 5;
		@Tooltip @Nullable public ResourceKey<Level> escapeTargetWorld = Level.OVERWORLD;


		public boolean shouldUseLimbo(ResourceKey<Level> level) {
			return worldsLeadingToLimbo.blacklist != worldsLeadingToLimbo.list.contains(level.location().toString());
		}

		public static final class WorldList {
			@Tooltip public List<String> list;
			@Tooltip public boolean blacklist;

			public WorldList(List<String> list, boolean blacklist) {
				this.list = list;
				this.blacklist = blacklist;
			}
			
			public WorldList() {
				this(new ArrayList<>(), false);
			}
		}
	}

	public static class Decay {
		@Tooltip public double decaySpreadChance = 1.0;

		@Tooltip public int decayDelay = 40;
		public boolean decaysIntoAir = true;
	}

	public static class Graphics {
		@Tooltip public boolean showRiftCore = false;
		@Tooltip public int highlightRiftCoreFor = 15000;
		@Tooltip public double riftSize = 1;
		@Tooltip public double riftJitter = 1;
	}

//	@EnvironmentInterface(value = EnvType.CLIENT, itf = SelectionListEntry.Translatable.class)
//	public enum ExtendedResourcePackActivationType implements SelectionListEntry.Translatable {
//		NORMAL(ResourcePackActivationType.NORMAL, "resourcePackActivationType.normal"),
//		DEFAULT_ENABLED(ResourcePackActivationType.DEFAULT_ENABLED, "resourcePackActivationType.defaultEnabled"),
//		ALWAYS_ENABLED(ResourcePackActivationType.ALWAYS_ENABLED, "resourcePackActivationType.alwaysEnabled");
//
//		private final ResourcePackActivationType resourcePackActivationType;
//		private final String translationKey;
//
//		ExtendedResourcePackActivationType(ResourcePackActivationType resourcePackActivationType, String translationKey) {
//			this.resourcePackActivationType = resourcePackActivationType;
//			this.translationKey = translationKey;
//		}
//
//		public ResourcePackActivationType asResourcePackActivationType() {
//			return resourcePackActivationType;
//		}
//
//		@Override
//		public @NotNull String getKey() {
//			return translationKey;
//		}
//	}

	public static class SubRootGsonConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
		private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(new TypeToken<ResourceKey<Level>>() {}.getType(), new LevelKeyAdapter()).create();
		private final Config definition;
		private final Class<T> configClass;

		public SubRootGsonConfigSerializer(Config definition, Class<T> configClass) {
			this.definition = definition;
			this.configClass = configClass;
		}

		private Path getConfigPath() {
			return DimensionalDoors.getConfigRoot().resolve(definition.name() + "-config.json5");
		}

		@Override
		public void serialize(T config) throws SerializationException {
			Path configPath = getConfigPath();
			try {
				Files.createDirectories(configPath.getParent());
				BufferedWriter writer = Files.newBufferedWriter(configPath);
				writer.write(GSON.toJson(config));
				writer.close();
			} catch (IOException e) {
				throw new SerializationException(e);
			}
		}

		@Override
		public T deserialize() throws SerializationException {
			Path configPath = getConfigPath();
			if (Files.exists(configPath)) {
				try (var reader = Files.newBufferedReader(getConfigPath())) {
					return GSON.fromJson(reader, configClass);
				} catch (Throwable e) {
					throw new SerializationException(e);
				}
			} else {
				return createDefault();
			}
		}

		@Override
		public T createDefault() {
			return Utils.constructUnsafely(configClass);
		}
	}

	public static final class LevelKeyAdapter implements JsonSerializer<ResourceKey<Level>>, JsonDeserializer<ResourceKey<Level>> {

		@Override
		public ResourceKey<Level> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(json.getAsJsonPrimitive().getAsString()));
		}

		@Override
		public JsonElement serialize(ResourceKey<Level> src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.location().toString());
		}
	}
}