package org.dimdev.dimdoors.shared;

import lombok.Getter;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dimdev.dimdoors.DimDoors;

import net.minecraftforge.common.config.Config;

import java.io.File;

@Config(modid= DimDoors.MODID,name=DimDoors.MODID+"/"+DimDoors.MODID, category="")
@Mod.EventBusSubscriber(modid=DimDoors.MODID)
public class ModConfig {

    @Config.Comment({ "General Config Options" })
    public static General general = new General();
    @Config.Comment({ "Pocket Config Options" })
    public static Pocket pocket = new Pocket();
    @Config.Comment({ "World Generation Config Options" })
    public static WorldGen world = new WorldGen();
    @Config.Comment({ "Dungeon Config Options" })
    public static Dungeons dungeon = new Dungeons();
    @Config.Comment({ "Monolith Config Options" })
    public static Monoliths monolith = new Monoliths();

    public static class General {
        @Config.Name("Dimension ID")
        @Config.Comment({""})
        @Getter public int dimensionID = 684;

        @Config.Name("Status Messages for Players")
        @Config.Comment({""})
        @Getter public boolean actionMsg = true;

        @Config.Name("Doors Close Behind Players")
        @Config.Comment({""})
        @Getter public boolean closeDoorBehind = true;
    }

    public static class Pocket {
        @Config.Name("Pocket Grid Size")
        @Config.Comment({""})
        @Getter public int pocketGridSize = 32;

        @Config.Name("Max Pocket Size")
        @Config.Comment({""})
        @Getter public int maxPocketSize = 15;

        @Config.Name("Private Pocket Size")
        @Config.Comment({""})
        @Getter public int privatePocketSize = 2;

        @Config.Name("Public Pocket Size")
        @Config.Comment({""})
        @Getter public int publicPocketSize = 1;

        @Config.Name("Load All Schematics")
        @Config.Comment({""})
        @Getter public boolean loadAllSchematics = false;
    }

    public static class WorldGen {
        @Config.Name("Rift Cluster Generation Chance")
        @Config.Comment({""})
        @Config.RangeDouble(min=0, max=1)
        @Getter public double clusterGenerationChance = 0.0002;

        @Config.Name("Rift Cluster Dimension Blacklist")
        @Config.Comment({""})
        @Getter public int[] riftClusterDimensionBlacklist = {};

        @Config.Name("Gateway Generation Chance")
        @Config.Comment({""})
        @Config.RangeDouble(min=0, max=1)
        @Getter public double gatewayGenerationChance = 0.0015;

        @Config.Name("Gateway Dimension Blacklist")
        @Config.Comment({""})
        @Getter public int[] gatewayDimensionBlacklist = {};
    }

    public static class Dungeons {
        @Config.Name("Maximum Dungeon Depth")
        @Config.Comment({""})
        @Config.RangeInt(min = 1, max = 128)
        @Getter public int maxDungeonDepth = 100;
    }

    public static class Monoliths {
        @Config.Name("Dangerous Monoliths")
        @Config.Comment({""})
        @Getter public boolean dangerousLimboMonolithsEnabled = false;

        @Config.Name("Monolith Teleportation")
        @Config.Comment({""})
        @Getter public boolean monolithTeleportationEnabled = true;

        @Config.Name("Universal Limbo")
        @Config.Comment({""})
        @Getter public boolean universalLimboEnabled = false;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DimDoors.MODID)) {
            ConfigManager.sync(event.getModID(), Config.Type.INSTANCE); // Sync config values
        }
    }

}
