package org.dimdev.dimdoors.compat.clothconfig;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ClothConfigCompat {

    public static Screen createScreen(Screen parent) {
            var config = DimensionalDoors.getConfig();

            var builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("dimdoors.config.title"))
                    .setDoesConfirmSave(true)
                    .setSavingRunnable(DimensionalDoors::saveConfig);
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            var general = config.getGeneralConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.general"))
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
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.pockets"))
                    .addEntry(createInt(entryBuilder, "pockets.pocketGridSize", pockets.pocketGridSize, 32, value -> pockets.pocketGridSize = value))
                    .addEntry(createInt(entryBuilder, "pockets.maxPocketSize", pockets.maxPocketSize, 15, value -> pockets.maxPocketSize = value))
                    .addEntry(createInt(entryBuilder, "pockets.privatePocketSize", pockets.privatePocketSize, 2, value -> pockets.privatePocketSize = value))
                    .addEntry(createInt(entryBuilder, "pockets.publicPocketSize", pockets.publicPocketSize, 1, value -> pockets.publicPocketSize = value))
                    .addEntry(createBoolean(entryBuilder, "pockets.canUseRiftSignatureInPrivatePockets", pockets.canUseRiftSignatureInPrivatePockets, true, value -> pockets.canUseRiftSignatureInPrivatePockets = value))
                    .addEntry(createInt(entryBuilder, "pockets.blocksColoredPerDye", pockets.blocksColoredPerDye, 10, value -> pockets.blocksColoredPerDye = value));

            var world = config.getWorldConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.world"))
                    .addEntry(createDouble(entryBuilder, "world.clusterGenChance", world.clusterGenChance, 20000, value -> world.clusterGenChance = value))
                    .addEntry(createStringList(entryBuilder, "world.clusterDimBlacklist", world.clusterDimBlacklist, List.of(), value -> {
                        world.clusterDimBlacklist.clear();
                        world.clusterDimBlacklist.addAll(value);
                    }))
                    .addEntry(createStringList(entryBuilder, "world.gatewayDimBlacklist", world.gatewayDimBlacklist, List.of(), value -> {
                        world.gatewayDimBlacklist.clear();
                        world.gatewayDimBlacklist.addAll(value);
                    }));

            var dungeons = config.getDungeonsConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.dungeons"))
                    .addEntry(createInt(entryBuilder, "dungeons.maxDungeonDepth", dungeons.maxDungeonDepth, 50, value -> dungeons.maxDungeonDepth = value));

            var monoliths = config.getMonolithsConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.monoliths"))
                    .addEntry(createBoolean(entryBuilder, "monoliths.dangerousLimboMonoliths", monoliths.dangerousLimboMonoliths, false, value -> monoliths.dangerousLimboMonoliths = value))
                    .addEntry(createBoolean(entryBuilder, "monoliths.monolithTeleportation", monoliths.monolithTeleportation, true, value -> monoliths.monolithTeleportation = value));

            var limbo = config.getLimboConfig();
            var worldsLeadingToLimbo = limbo.getWorldsLeadingToLimbo();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.limbo"))
                    .addEntry(createBoolean(entryBuilder, "limbo.worldsLeadingToLimbo.blacklist", worldsLeadingToLimbo.blacklist, false, value -> worldsLeadingToLimbo.blacklist = value))
                    .addEntry(createLevelKeyList(entryBuilder, "limbo.worldsLeadingToLimbo.list", worldsLeadingToLimbo.list, List.of(), value -> {
                        worldsLeadingToLimbo.list.clear();
                        worldsLeadingToLimbo.list.addAll(value);
                    }))
                    .addEntry(createBoolean(entryBuilder, "limbo.hardcoreLimbo", limbo.hardcoreLimbo, false, value -> limbo.hardcoreLimbo = value))
                    .addEntry(createInt(entryBuilder, "limbo.limboReturnDistanceMax", limbo.limboReturnDistanceMax, 200, value -> limbo.limboReturnDistanceMax = value))
                    .addEntry(createInt(entryBuilder, "limbo.limboReturnDistanceMin", limbo.limboReturnDistanceMin, 100, value -> limbo.limboReturnDistanceMin = value))
                    .addEntry(createBoolean(entryBuilder, "limbo.decaySurroundings", limbo.decaySurroundings, false, value -> limbo.decaySurroundings = value))
                    .addEntry(createBoolean(entryBuilder, "limbo.tryPlayerBedSpawn", limbo.tryPlayerBedSpawn, false, value -> limbo.tryPlayerBedSpawn = value))
                    .addEntry(createBoolean(entryBuilder, "limbo.defaultToWorldSpawn", limbo.defaultToWorldSpawn, true, value -> limbo.defaultToWorldSpawn = value))
                    .addEntry(createFloat(entryBuilder, "limbo.limboBlocksCorruptingExitWorldAmount", limbo.limboBlocksCorruptingExitWorldAmount, 5.0F, value -> limbo.limboBlocksCorruptingExitWorldAmount = value))
                    .addEntry(createLevelKey(entryBuilder, "limbo.escapeTargetWorld", limbo.escapeTargetWorld, Level.OVERWORLD, value -> limbo.escapeTargetWorld = value));

            var graphics = config.getGraphicsConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.graphics"))
                    .addEntry(createBoolean(entryBuilder, "graphics.showRiftCore", graphics.showRiftCore, false, value -> graphics.showRiftCore = value))
                    .addEntry(createInt(entryBuilder, "graphics.highlightRiftCoreFor", graphics.highlightRiftCoreFor, 15000, value -> graphics.highlightRiftCoreFor = value))
                    .addEntry(createDouble(entryBuilder, "graphics.riftSize", graphics.riftSize, 1, value -> graphics.riftSize = value))
                    .addEntry(createDouble(entryBuilder, "graphics.riftJitter", graphics.riftJitter, 1, value -> graphics.riftJitter = value));

            var doors = config.getDoorsConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.doors"))
                    .addEntry(createBoolean(entryBuilder, "doors.closeDoorBehind", doors.closeDoorBehind, true, value -> doors.closeDoorBehind = value))
                    .addEntry(createEnum(entryBuilder, "doors.doorList.mode", ModConfig.Doors.DoorList.Mode.class, doors.doorList.mode, ModConfig.Doors.DoorList.Mode.DISABLE, value -> doors.doorList.mode = value))
                    .addEntry(createStringList(entryBuilder, "doors.doorList.doors", doors.doorList.doors, List.of(), value -> {
                        doors.doorList.doors.clear();
                        doors.doorList.doors.addAll(value);
                    }))
                    .addEntry(createBoolean(entryBuilder, "doors.placeRiftsInCreativeMode", doors.placeRiftsInCreativeMode, true, value -> doors.placeRiftsInCreativeMode = value));

            var decay = config.getDecayConfig();
            builder.getOrCreateCategory(Component.translatable("dimdoors.config.category.decay"))
                    .addEntry(createDouble(entryBuilder, "decay.decaySpreadChance", decay.decaySpreadChance, 1.0, value -> decay.decaySpreadChance = value))
                    .addEntry(createInt(entryBuilder, "decay.decayDelay", decay.decayDelay, 40, value -> decay.decayDelay = value))
                    .addEntry(createBoolean(entryBuilder, "decay.decaysIntoAir", decay.decaysIntoAir, true, value -> decay.decaysIntoAir = value));

            return builder.build();
    }

    private static @NotNull AbstractConfigListEntry<?> createDouble(ConfigEntryBuilder builder, String name, double value, double defaultValue, Consumer<Double> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startDoubleField(Component.translatable(langEntry), value)
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }

    private static @NotNull AbstractConfigListEntry<?> createFloat(ConfigEntryBuilder builder, String name, float value, float defaultValue, Consumer<Float> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startFloatField(Component.translatable(langEntry), value)
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }

    private static @NotNull AbstractConfigListEntry<?> createBoolean(ConfigEntryBuilder builder, String name, boolean value, boolean defaultValue, Consumer<Boolean> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startBooleanToggle(Component.translatable(langEntry), value)
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }

    private static @NotNull AbstractConfigListEntry<?> createInt(ConfigEntryBuilder builder, String name, int value, int defaultValue, Consumer<Integer> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startIntField(Component.translatable(langEntry), value)
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }

    private static <T extends Enum<T>> @NotNull AbstractConfigListEntry<?> createEnum(ConfigEntryBuilder builder, String name, Class<T> enumClass, T value, T defaultValue, Consumer<T> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startEnumSelector(Component.translatable(langEntry), enumClass, value)
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(consumer)
                .build();
    }

    private static @NotNull AbstractConfigListEntry<?> createStringList(ConfigEntryBuilder builder, String name, Collection<String> value, Collection<String> defaultValue, Consumer<List<String>> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startStrList(Component.translatable(langEntry), new ArrayList<>(value))
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(new ArrayList<>(defaultValue))
                .setSaveConsumer(consumer)
                .build();
    }

    private static @NotNull AbstractConfigListEntry<?> createLevelKey(ConfigEntryBuilder builder, String name, @Nullable ResourceKey<Level> value, @Nullable ResourceKey<Level> defaultValue, Consumer<ResourceKey<Level>> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startStrField(Component.translatable(langEntry), levelKeyToString(value))
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(levelKeyToString(defaultValue))
                .setSaveConsumer(rawValue -> consumer.accept(stringToLevelKey(rawValue)))
                .build();
    }

    private static @NotNull AbstractConfigListEntry<?> createLevelKeyList(ConfigEntryBuilder builder, String name, List<ResourceKey<Level>> value, List<ResourceKey<Level>> defaultValue, Consumer<List<ResourceKey<Level>>> consumer) {
        var langEntry = "dimdoors.config.option." + name;

        return builder
                .startStrList(Component.translatable(langEntry), levelKeysToStrings(value))
                .setTooltip(Component.translatable(langEntry + ".tooltip"))
                .setDefaultValue(levelKeysToStrings(defaultValue))
                .setSaveConsumer(rawValues -> consumer.accept(stringsToLevelKeys(rawValues)))
                .build();
    }

    private static String levelKeyToString(@Nullable ResourceKey<Level> key) {
        return key == null ? "" : key.location().toString();
    }

    private static @Nullable ResourceKey<Level> stringToLevelKey(String value) {
        var trimmed = value.trim();

        if (trimmed.isEmpty()) {
            return null;
        }

        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(trimmed));
    }

    private static List<String> levelKeysToStrings(List<ResourceKey<Level>> keys) {
        var strings = new ArrayList<String>();

        for (var key : keys) {
            strings.add(levelKeyToString(key));
        }

        return strings;
    }

    private static List<ResourceKey<Level>> stringsToLevelKeys(List<String> values) {
        var keys = new ArrayList<ResourceKey<Level>>();

        for (var value : values) {
            var key = stringToLevelKey(value);

            if (key != null) {
                keys.add(key);
            }
        }

        return keys;
    }
}