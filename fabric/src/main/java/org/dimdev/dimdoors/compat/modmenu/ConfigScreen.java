package org.dimdev.dimdoors.compat.modmenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.config.Option;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ConfigScreen {

    public static Screen createScreen(Screen parent) {
        if(DimensionalDoors.getSided().isModLoaded("cloth_config")) {
            var config = DimensionalDoors.getConfig();

            var builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("dimdoors.config.title"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();;

            var general = config.getGeneralConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.decay"))
                    .addEntry(createDouble(entryBuilder, "general.teleportOffset", general.teleportOffset, 0, value -> general.teleportOffset = value))
                    .addEntry(createBoolean(entryBuilder, "general.riftBoundingBoxInCreative", general.riftBoundingBoxInCreative, false, value -> general.riftBoundingBoxInCreative = value))
                    .addEntry(createDouble(entryBuilder, "general.riftCloseSpeed", general.riftCloseSpeed, 0.1, value -> general.riftCloseSpeed = value))
                    .addEntry(createDouble(entryBuilder, "general.riftGrowthSpeed", general.riftGrowthSpeed, 1, value -> general.riftGrowthSpeed = value))
                    .addEntry(createBoolean(entryBuilder, "general.enableRiftDecay", general.enableRiftDecay, true, value -> general.enableRiftDecay = value))
                    .addEntry(createInt(entryBuilder, "general.depthSpreadFactor", general.depthSpreadFactor, 20, value -> general.depthSpreadFactor = value))
                    .addEntry(createDouble(entryBuilder, "general.endermanSpawnChance", general.endermanSpawnChance, 0.00005, value -> general.endermanSpawnChance = value))
                    .addEntry(createDouble(entryBuilder, "general.endermanAggressiveChance", general.endermanAggressiveChance, 0.5, value -> general.endermanAggressiveChance = value))
                    .addEntry(createBoolean(entryBuilder, "general.enableDebugMessages", general.enableDebugMessages, false, value -> general.enableDebugMessages = value));

            var pockets = config.getPocketsConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.doors"))
                    .addEntry(createInt(entryBuilder, "pockets.pocketGridSize", pockets.pocketGridSize, 32, value -> pockets.pocketGridSize = value))
                    .addEntry(createInt(entryBuilder, "pockets.maxPocketSize", pockets.maxPocketSize, 15, value -> pockets.maxPocketSize = value))
                    .addEntry(createInt(entryBuilder, "pockets.privatePocketSize", pockets.privatePocketSize, 2, value -> pockets.privatePocketSize = value))
                    .addEntry(createInt(entryBuilder, "pockets.publicPocketSize", pockets.publicPocketSize, 1, value -> pockets.publicPocketSize = value))
                    .addEntry(createBoolean(entryBuilder, "pockets.canUseRiftSignatureInPrivatePockets", pockets.canUseRiftSignatureInPrivatePockets, true, value -> pockets.canUseRiftSignatureInPrivatePockets = value))
                    .addEntry(createInt(entryBuilder, "pockets.blocksColoredPerDye", pockets.blocksColoredPerDye, 10, value -> pockets.blocksColoredPerDye = value));

            var world = config.getWorldConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.dungeons"))

            var dungeons = config.getDungeonsConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.general"))
                    .addEntry(createInt(entryBuilder, "dungeons.maxDungeonDepth", dungeons.maxDungeonDepth, 50, value -> dungeons.maxDungeonDepth = value));

            var monoliths = config.getMonolithsConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.graphics"))
                    .addEntry(createBoolean(entryBuilder, "monoliths.dangerousLimboMonoliths", monoliths.dangerousLimboMonoliths, false, value -> monoliths.dangerousLimboMonoliths = value))
                    .addEntry(createBoolean(entryBuilder, "monoliths.monolithTeleportation", monoliths.monolithTeleportation, true, value -> monoliths.monolithTeleportation = value));

            var limbo = config.getLimboConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.limbo"))

            var graphics = config.getGraphicsConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.monoliths"))
                    .addEntry(createBoolean(entryBuilder, "graphics.showRiftCore", graphics.showRiftCore, false, value -> graphics.showRiftCore = value))
                    .addEntry(createInt(entryBuilder, "graphics.highlightRiftCoreFor", graphics.highlightRiftCoreFor, 15000, value -> graphics.highlightRiftCoreFor = value))
                    .addEntry(createDouble(entryBuilder, "graphics.riftSize", graphics.riftSize, 1, value -> graphics.riftSize = value))
                    .addEntry(createDouble(entryBuilder, "graphics.riftJitter", graphics.riftJitter, 1, value -> graphics.riftJitter = value));

            var doors = config.getDoorsConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.pockets"))

            var decay = config.getDecayConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.world"))
                    .addEntry(createDouble(entryBuilder, "decay.decaySpreadChance", decay.decaySpreadChance, 1.0, value -> decay.decaySpreadChance = value))
                    .addEntry(createInt(entryBuilder, "decay.decayDelay", decay.decayDelay, 40, value -> decay.decayDelay = value))
                    .addEntry(createBoolean(entryBuilder, "decay.decaysIntoAir", decay.decaysIntoAir, true, value -> decay.decaysIntoAir = value));

            return builder.build();
        } else {
            return null;
        }
    }
    private static @NotNull DoubleListEntry createDouble(ConfigEntryBuilder builder, String name, double value, double defaultValue, Consumer<Double> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startDoubleField(Component.translatable(langEntry), value)
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }

    private static @NotNull BooleanListEntry createBoolean(ConfigEntryBuilder builder, String name, boolean value, boolean defaultValue, Consumer<Boolean> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startBooleanToggle(Component.translatable(langEntry), value)
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }

    private static @NotNull IntegerListEntry createInt(ConfigEntryBuilder builder, String name, int value, int defaultValue, Consumer<Integer> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startIntField(Component.translatable(langEntry), value)
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }
}
