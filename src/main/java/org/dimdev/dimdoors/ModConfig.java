package org.dimdev.dimdoors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;

import static me.shedaniel.autoconfig.annotation.ConfigEntry.*;
import static me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.*;
import static me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON;

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
	@Category("player")
	private Player player = new Player();
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

	public General getGeneralConfig() {
		return this.general;
	}

	public Pockets getPocketsConfig() {
		return this.pockets;
	}

	public World getWorldConfig() {
		return this.world;
	}

	public Player getPlayerConfig() {
		return this.player;
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

	public static class General {
		@Tooltip public double teleportOffset = 0.5;
		@Tooltip public boolean riftBoundingBoxInCreative;
		@Tooltip public double riftCloseSpeed = 0.01;
		@Tooltip public double riftGrowthSpeed = 1;
		@Tooltip public int depthSpreadFactor = 20;
		@Tooltip public double endermanSpawnChance = 0.00005;
		@Tooltip public double endermanAggressiveChance = 0.5;
	}

	public static class Doors {
		@Tooltip public boolean closeDoorBehind = false;
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

		public boolean isAllowed(Identifier id) {
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
		@Tooltip @EnumHandler(option = BUTTON) public ExtendedResourcePackActivationType classicPocketsResourcePackActivationType = ExtendedResourcePackActivationType.DEFAULT_ENABLED;
		@Tooltip @EnumHandler(option = BUTTON) public ExtendedResourcePackActivationType defaultPocketsResourcePackActivationType = ExtendedResourcePackActivationType.DEFAULT_ENABLED;
		@Tooltip public boolean asyncWorldEditPocketLoading = true;
		@Tooltip public boolean canUseRiftSignatureInPrivatePockets = true;
	}

	public static class World {
		@RequiresRestart
		@Tooltip public double clusterGenChance = 20000;
		@RequiresRestart
		@Tooltip public int gatewayGenChance = 200;
		@RequiresRestart
		@Tooltip public List<String> clusterDimBlacklist = new LinkedList<>();
		@RequiresRestart
		@Tooltip public List<String> gatewayDimBlacklist = new LinkedList<>();
	}

	public static class Player {
		@CollapsibleObject @Tooltip public Fray fray = new Fray();

		public static class Fray {
			@Tooltip public int maxFray = 200;
			@Tooltip public int riftFrayIncrement = 5;
			@Tooltip public int monolithTeleportationIncrement = 5;
			@Tooltip public int eternalFluidFrayDecrease = 100;
			@Tooltip public int nameplateGlitchFray = 80;
			@Tooltip public int staticOverlayFray = 100;
			@Tooltip public int armorDamageFray = 125;
			@Tooltip public int grayScreenFray = 175;
			@Tooltip public int unravelledStatueFray = 200;
			@Tooltip public int minFrayForTickFray = 125;
			@Tooltip public int unravledFabricInInventoryFray = 150;
		}
	}

	public static class Dungeons {
		@Tooltip public int maxDungeonDepth = 50;
	}

	public static class Monoliths {
		@Tooltip public boolean dangerousLimboMonoliths = false;
		@Tooltip public boolean monolithTeleportation = true;
	}

	public static class Limbo {
		@Tooltip public boolean universalLimbo = false;
		@Tooltip public boolean hardcoreLimbo = false;
		@Tooltip public double decaySpreadChance = 0.5;
		@Tooltip public float limboBlocksCorruptingOverworldAmount = 5;
	}

	public static class Graphics {
		@Tooltip public boolean showRiftCore = false;
		@Tooltip public int highlightRiftCoreFor = 15000;
		@Tooltip public double riftSize = 1;
		@Tooltip public double riftJitter = 1;
	}

	@EnvironmentInterface(value = EnvType.CLIENT, itf = SelectionListEntry.Translatable.class)
	public enum ExtendedResourcePackActivationType implements SelectionListEntry.Translatable {
		NORMAL(ResourcePackActivationType.NORMAL, "resourcePackActivationType.normal"),
		DEFAULT_ENABLED(ResourcePackActivationType.DEFAULT_ENABLED, "resourcePackActivationType.defaultEnabled"),
		ALWAYS_ENABLED(ResourcePackActivationType.ALWAYS_ENABLED, "resourcePackActivationType.alwaysEnabled");

		private final ResourcePackActivationType resourcePackActivationType;
		private final String translationKey;

		ExtendedResourcePackActivationType(ResourcePackActivationType resourcePackActivationType, String translationKey) {
			this.resourcePackActivationType = resourcePackActivationType;
			this.translationKey = translationKey;
		}

		public ResourcePackActivationType asResourcePackActivationType() {
			return resourcePackActivationType;
		}

		@Override
		public @NotNull String getKey() {
			return translationKey;
		}
	}

	public static class SubRootJanksonConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
		private static final Jankson JANKSON = Jankson.builder().build();
		private final Config definition;
		private final Class<T> configClass;

		public SubRootJanksonConfigSerializer(Config definition, Class<T> configClass) {
			this.definition = definition;
			this.configClass = configClass;
		}

		private Path getConfigPath() {
			return DimensionalDoorsInitializer.getConfigRoot().resolve(definition.name() + "-config.json5");
		}

		@Override
		public void serialize(T config) throws SerializationException {
			Path configPath = getConfigPath();
			try {
				Files.createDirectories(configPath.getParent());
				BufferedWriter writer = Files.newBufferedWriter(configPath);
				writer.write(JANKSON.toJson(config).toJson(true, true));
				writer.close();
			} catch (IOException e) {
				throw new SerializationException(e);
			}
		}

		@Override
		public T deserialize() throws SerializationException {
			Path configPath = getConfigPath();
			if (Files.exists(configPath)) {
				try {
					return JANKSON.fromJson(JANKSON.load(getConfigPath().toFile()), configClass);
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
}
