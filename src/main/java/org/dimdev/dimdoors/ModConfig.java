package org.dimdev.dimdoors;

import java.util.LinkedList;
import java.util.List;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Category;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.RequiresRestart;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Tooltip;
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
		@Tooltip public boolean closeDoorBehind = false;
		@Tooltip public double teleportOffset = 0.5;
		@Tooltip public boolean riftBoundingBoxInCreative;
		@Tooltip public double riftCloseSpeed = 0.005;
		@Tooltip public double riftGrowthSpeed = 1;
		@Tooltip public int depthSpreadFactor = 20;
		@Tooltip public double endermanSpawnChance = 0.001;
		@Tooltip public double endermanAggressiveChance = 0.5;
	}

	public static class Pockets {
		@Tooltip public int pocketGridSize = 32;
		@Tooltip public int maxPocketSize = 15;
		@Tooltip public int privatePocketSize = 2;
		@Tooltip public int publicPocketSize = 1;
		@Tooltip public String defaultWeightEquation = "5";
		@Tooltip public int fallbackWeight = 5;
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
	}

	public static class Graphics {
		@Tooltip public boolean showRiftCore = false;
		@Tooltip public int highlightRiftCoreFor = 15000;
		@Tooltip public double riftSize = 1;
		@Tooltip public double riftJitter = 1;
	}
}
