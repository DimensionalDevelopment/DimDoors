package org.dimdev.dimdoors.compat.clothconfig

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.ModConfig.Doors
import org.dimdev.dimdoors.item.translate

object ClothConfigCompat {
    @JvmStatic
    fun createScreen(parent: Screen?): Screen? {
        val config = config

        val builder = ConfigBuilder.create().apply {
            parentScreen = parent
            title = "config.dimdoors.title".translate()
            setDoesConfirmSave(true)
            setSavingRunnable { DimensionalDoors.saveConfig() }
        }
        val entryBuilder = builder.entryBuilder()

        var currentCategory = ""

        fun category(name: String, block: ConfigCategory.() -> Unit) {
            currentCategory = name
            builder.getOrCreateCategory("config.dimdoors.$currentCategory.category".translate()).also(block)
        }

        fun ConfigCategory.double(name: String, value: Double, defaultValue: Double, consumer: (Double) -> Unit): ConfigCategory = this.addEntry(entryBuilder.createDouble(currentCategory, name, value, defaultValue, consumer))
        fun ConfigCategory.boolean(name: String, value: Boolean, defaultValue: Boolean, consumer: (Boolean) -> Unit): ConfigCategory = this.addEntry(entryBuilder.createBoolean(currentCategory, name, value, defaultValue, consumer))
        fun ConfigCategory.integer(name: String, value: Int, defaultValue: Int, consumer: (Int) -> Unit): ConfigCategory = this.addEntry(entryBuilder.createInt(currentCategory, name, value, defaultValue, consumer))
        fun ConfigCategory.float(name: String, value: Float, defaultValue: Float, consumer: (Float) -> Unit): ConfigCategory = this.addEntry(entryBuilder.createFloat(currentCategory, name, value, defaultValue, consumer))
        fun ConfigCategory.stringList(name: String, value: MutableList<String>, defaultValue: MutableList<String>, consumer: (MutableList<String>) -> Unit): ConfigCategory = this.addEntry(entryBuilder.createStringList(currentCategory, name, value, defaultValue, consumer))
        fun ConfigCategory.resourceLocationList(name: String, value: MutableList<ResourceLocation>, defaultValue: MutableList<ResourceLocation>, consumer: (MutableList<ResourceLocation>) -> Unit): ConfigCategory = this.addEntry(entryBuilder.createResouceLocationList(currentCategory, name, value, defaultValue, consumer))
        fun ConfigCategory.levelKey(name: String, value: ResourceKey<Level>, defaultValue: ResourceKey<Level>, consumer: (ResourceKey<Level>) -> Unit): ConfigCategory = this.addEntry(entryBuilder.createLevelKey(currentCategory, name, value, defaultValue, consumer))
        fun ConfigCategory.levelKeyList(name: String, value: MutableList<ResourceKey<Level>>, defaultValue: MutableList<ResourceKey<Level>>, consumer: (MutableList<ResourceKey<Level>>) -> Unit): ConfigCategory = this.addEntry(entryBuilder.createLevelKeyList(currentCategory, name, value, defaultValue, consumer))
        fun <T: Enum<T>> ConfigCategory.enum(name: String, clazz: Class<T>, value: T, defaultValue: T, consumer: (T) -> Unit): ConfigCategory = this.addEntry(entryBuilder.createEnum(currentCategory, name, clazz, value, defaultValue, consumer))


        val general = config.generalConfig

        category("general") {
            double("teleportOffset", general.teleportOffset, 0.0) { general.teleportOffset = it }
            boolean("riftBoundingBoxInCreative", general.riftBoundingBoxInCreative, false) { general.riftBoundingBoxInCreative = it }
            double("riftCloseSpeed", general.riftCloseSpeed, 0.1) { general.riftCloseSpeed = it }
            double("riftGrowthSpeed", general.riftGrowthSpeed, 1.0) { general.riftGrowthSpeed = it }
            boolean("enableRiftDecay", general.enableRiftDecay, true) { general.enableRiftDecay = it }
            integer("depthSpreadFactor", general.depthSpreadFactor, 20) { general.depthSpreadFactor = it }
            double("endermanSpawnChance", general.endermanSpawnChance, 0.00005) { general.endermanSpawnChance = it }
            double("endermanAggressiveChance", general.endermanAggressiveChance, 0.5) { general.endermanAggressiveChance = it }
            boolean("enableDebugMessages", general.enableDebugMessages, false) { general.enableDebugMessages = it }
        }

        val pockets = config.pocketsConfig

        category("pockets") {
            integer("pocketGridSize", pockets.pocketGridSize, 32) { pockets.pocketGridSize = it }
            integer("maxPocketSize", pockets.maxPocketSize, 15) { pockets.maxPocketSize = it }
            integer("privatePocketSize", pockets.privatePocketSize, 2) { pockets.privatePocketSize = it }
            integer("publicPocketSize", pockets.publicPocketSize, 1) { pockets.publicPocketSize = it }
            integer("blocksColoredPerDye", pockets.blocksColoredPerDye, 10) { pockets.blocksColoredPerDye = it }
        }

        val world = config.worldConfig

        category("world") {
            double("clusterGenChance", world.clusterGenChance, 20000.0) { world.clusterGenChance = it }
            stringList("clusterDimBlacklist", world.clusterDimBlacklist, mutableListOf()) { world.clusterDimBlacklist = it }
            stringList("gatewayDimBlacklist", world.gatewayDimBlacklist, mutableListOf()) { world.gatewayDimBlacklist = it }
        }

        val dungeons = config.dungeonsConfig

        category("dungeons") {
            integer("maxDungeonDepth", dungeons.maxDungeonDepth, 50) { dungeons.maxDungeonDepth = it }
        }

        val monoliths = config.monolithsConfig

        category("monoliths") {
            boolean( "dangerousLimboMonoliths", monoliths.dangerousLimboMonoliths, false) { monoliths.dangerousLimboMonoliths = it }
            boolean("monolithTeleportation", monoliths.monolithTeleportation, true){ monoliths.monolithTeleportation = it }
        }

        val limbo = config.limboConfig
        val worldsLeadingToLimbo = limbo.worldsLeadingToLimbo

        category("limbo") {
            boolean("worldsLeadingToLimbo.blacklist", worldsLeadingToLimbo.blacklist, false) { worldsLeadingToLimbo.blacklist = it }
            levelKeyList("worldsLeadingToLimbo.list", worldsLeadingToLimbo.list, mutableListOf()) { worldsLeadingToLimbo.list = it }
            boolean("hardcoreLimbo", limbo.hardcoreLimbo, false) { limbo.hardcoreLimbo = it }
            integer("limboReturnDistanceMax", limbo.limboReturnDistanceMax, 200) { limbo.limboReturnDistanceMax = it }
            integer("limboReturnDistanceMin", limbo.limboReturnDistanceMin, 100) { limbo.limboReturnDistanceMin = it }

            boolean("decaySurroundings", limbo.decaySurroundings, false) { limbo.decaySurroundings = it }
            boolean("tryPlayerBedSpawn", limbo.tryPlayerBedSpawn, false) { limbo.tryPlayerBedSpawn = it }
            boolean("defaultToWorldSpawn", limbo.defaultToWorldSpawn, true) { limbo.defaultToWorldSpawn = it }
            float("limboBlocksCorruptingExitWorldAmount", limbo.limboBlocksCorruptingExitWorldAmount, 5.0f) { limbo.limboBlocksCorruptingExitWorldAmount = it }
            levelKey("escapeTargetWorld", limbo.escapeTargetWorld, Level.OVERWORLD){ limbo.escapeTargetWorld = it }
            boolean("genericDeathMessages", limbo.genericDeathMessages, false) { limbo.genericDeathMessages = it }
        }

        val graphics = config.graphicsConfig

        category("graphics") {
            boolean("showRiftCore", graphics.showRiftCore, false) { graphics.showRiftCore = it }
            integer("highlightRiftCoreFor", graphics.highlightRiftCoreFor, 15000) { graphics.highlightRiftCoreFor = it }
            double("riftSize", graphics.riftSize, 1.0) { graphics.riftSize = it }
            double("riftJitter", graphics.riftJitter, 1.0) { graphics.riftJitter = it }
        }

        val doors = config.doorsConfig

        category("doors") {
            boolean("closeDoorBehind", doors.closeDoorBehind, true) { doors.closeDoorBehind = it }
            enum("doorList.mode", Doors.DoorList.Mode::class.java, doors.doorList.mode, Doors.DoorList.Mode.DISABLE) { doors.doorList.mode = it }
            resourceLocationList("doorList.doors", doors.doorList.doors, mutableListOf()) { doors.doorList.doors = it }
            boolean("placeRiftsInCreativeMode", doors.placeRiftsInCreativeMode, true) {  doors.placeRiftsInCreativeMode = it }
        }

        val decay = config.decayConfig

        category("decay") {
            double("decaySpreadChance", decay.decaySpreadChance, 1.0) { decay.decaySpreadChance = it }
            integer("decayDelay", decay.decayDelay, 40) { decay.decayDelay = it }

            boolean("decaysIntoAir", decay.decaysIntoAir, true) { decay.decaysIntoAir = it }
        }

        return builder.build()
    }

    private fun ConfigEntryBuilder.createDouble(
        category: String,
        name: String,
        value: Double,
        defaultValue: Double,
        consumer: (Double) -> Unit
    ): AbstractConfigListEntry<*> {
        val langEntry = "config.dimdoors.$category.option.$name"

        return this
            .startDoubleField(Component.translatable(langEntry), value)
            .setTooltip(Component.translatable("$langEntry.tooltip"))
            .setDefaultValue(defaultValue)
            .setSaveConsumer(consumer)
            .build()
    }

    private fun ConfigEntryBuilder.createFloat(
        category: String,
        name: String,
        value: Float,
        defaultValue: Float,
        consumer: (Float) -> Unit
    ): AbstractConfigListEntry<*> {
        val langEntry = "config.dimdoors.$category.option.$name"

        return this
            .startFloatField(Component.translatable(langEntry), value)
            .setTooltip(Component.translatable("$langEntry.tooltip"))
            .setDefaultValue(defaultValue)
            .setSaveConsumer(consumer)
            .build()
    }

    private fun ConfigEntryBuilder.createBoolean(
        category: String,
        name: String?,
        value: Boolean,
        defaultValue: Boolean,
        consumer: (Boolean) -> Unit
    ): AbstractConfigListEntry<*> {
        val langEntry = "config.dimdoors.$category.option.$name"

        return this
            .startBooleanToggle(Component.translatable(langEntry), value)
            .setTooltip(Component.translatable("$langEntry.tooltip"))
            .setDefaultValue(defaultValue)
            .setSaveConsumer(consumer)
            .build()
    }

    private fun ConfigEntryBuilder.createInt(
        category: String,
        name: String?,
        value: Int,
        defaultValue: Int,
        consumer: (Int) -> Unit
    ): AbstractConfigListEntry<*> {
        val langEntry = "config.dimdoors.$category.option.$name"

        return this
            .startIntField(Component.translatable(langEntry), value)
            .setTooltip(Component.translatable("$langEntry.tooltip"))
            .setDefaultValue(defaultValue)
            .setSaveConsumer(consumer)
            .build()
    }

    private fun <T : Enum<T>> ConfigEntryBuilder.createEnum(
        category: String,
        name: String?,
        enumClass: Class<T>,
        value: T,
        defaultValue: T,
        consumer: (T) -> Unit
    ): AbstractConfigListEntry<*> {
        val langEntry = "config.dimdoors.$category.option.$name"

        return this
            .startEnumSelector<T>(Component.translatable(langEntry), enumClass, value)
            .setTooltip(Component.translatable("$langEntry.tooltip"))
            .setDefaultValue(defaultValue)
            .setSaveConsumer(consumer)
            .build()
    }

    private fun ConfigEntryBuilder.createStringList(
        category: String,
        name: String,
        value: MutableList<String>,
        defaultValue: MutableList<String>,
        consumer: (MutableList<String>) -> Unit
    ): AbstractConfigListEntry<*> {
        val langEntry = "config.dimdoors.$category.option.$name"

        return this
            .startStrList(Component.translatable(langEntry), value)
            .setTooltip(Component.translatable("$langEntry.tooltip"))
            .setDefaultValue(defaultValue)
            .setSaveConsumer(consumer)
            .build()
    }

    private fun ConfigEntryBuilder.createResouceLocationList(
        category: String,
        name: String,
        value: MutableList<ResourceLocation>,
        defaultValue: MutableList<ResourceLocation>,
        consumer: (MutableList<ResourceLocation>) -> Unit
    ): AbstractConfigListEntry<*> {
        val langEntry = "config.dimdoors.$category.option.$name"

        val func: (MutableList<String>) -> Unit = {
            val list = it.map(ResourceLocation::parse).toMutableList()
            consumer(list)
        }

        return this
            .startStrList(Component.translatable(langEntry), value.map(ResourceLocation::toString))
            .setTooltip(Component.translatable("$langEntry.tooltip"))
            .setDefaultValue(defaultValue.map(ResourceLocation::toString))
            .setSaveConsumer(func)
            .build()
    }

    private fun ConfigEntryBuilder.createLevelKey(
        category: String,
        name: String,
        value: ResourceKey<Level>,
        defaultValue: ResourceKey<Level>,
        consumer: (ResourceKey<Level>) -> Unit
    ): AbstractConfigListEntry<*> {
        val langEntry = "config.dimdoors.$category.option.$name"

        return this
            .startStrField(Component.translatable(langEntry), levelKeyToString(value))
            .setTooltip(Component.translatable("$langEntry.tooltip"))
            .setDefaultValue(levelKeyToString(defaultValue))
            .setSaveConsumer { rawValue: String -> consumer.invoke(stringToLevelKey(rawValue) ?: defaultValue) }
            .build()
    }

    private fun ConfigEntryBuilder.createLevelKeyList(
        category: String,
        name: String,
        value: MutableList<ResourceKey<Level>>,
        defaultValue: MutableList<ResourceKey<Level>>,
        consumer: (MutableList<ResourceKey<Level>>) -> Unit
    ): AbstractConfigListEntry<*> {
        val langEntry = "config.dimdoors.$category.option.$name"

        return this
            .startStrList(Component.translatable(langEntry), levelKeysToStrings(value))
            .setTooltip(Component.translatable("$langEntry.tooltip"))
            .setDefaultValue(levelKeysToStrings(defaultValue))
            .setSaveConsumer { rawValues: MutableList<String> -> consumer.invoke(stringsToLevelKeys(rawValues)) }
            .build()
    }

    private fun levelKeyToString(key: ResourceKey<Level>): String {
        return key.location().toString()
    }

    private fun stringToLevelKey(value: String): ResourceKey<Level>? {
        val trimmed = value.trim { it <= ' ' }

        if (trimmed.isEmpty()) {
            return null
        }

        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(trimmed))
    }

    private fun levelKeysToStrings(keys: MutableList<ResourceKey<Level>>) = keys.map { levelKeyToString(it) }.toMutableList()

    private fun stringsToLevelKeys(values: MutableList<String>) =
        values.mapNotNull { stringToLevelKey(it) }.toMutableList()
}