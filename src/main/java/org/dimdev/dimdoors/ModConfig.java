package org.dimdev.dimdoors;

import java.util.LinkedList;
import java.util.List;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Category;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.RequiresRestart;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.TransitiveObject;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;

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
		public boolean closeDoorBehind = false;
		public double teleportOffset = 0.5;
		public boolean riftBoundingBoxInCreative;
		public double riftCloseSpeed = 0.005;
		public double riftGrowthSpeed = 1;
		public int depthSpreadFactor = 20;
		@RequiresRestart
		public boolean useEnderPearlsInCrafting = false;
		public double endermanSpawnChance = 0.001;
		public double endermanAggressiveChance = 0.5;
	}

	public static class Pockets {
		public int pocketGridSize = 32;
		public int maxPocketSize = 15;
		public int privatePocketSize = 2;
		public int publicPocketSize = 1;
		@RequiresRestart
		public boolean loadAllSchematics = false;
		@RequiresRestart
		public int cachedSchematics = 10;
	}

	public static class World {
		@RequiresRestart
		public double clusterGenChance = 0.0002;
		@RequiresRestart
		public int gatewayGenChance = 200;
		@RequiresRestart
		public List<Integer> clusterDimBlacklist = new LinkedList<>();
		@RequiresRestart
		public List<Integer> gatewayDimBlacklist = new LinkedList<>();
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

	public static void init() {
		DimensionalDoorsInitializer.CONFIG_MANAGER = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		DimensionalDoorsInitializer.CONFIG = DimensionalDoorsInitializer.CONFIG_MANAGER.getConfig();
	}
}
