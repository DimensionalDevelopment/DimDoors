package org.dimdev.dimdoors

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.util.ConfigType
import org.dimdev.dimdoors.util.CodecUtils.mutableList

data class ModConfig(
    val generalConfig: General = General(),
    val pocketsConfig: Pockets = Pockets(),
    val worldConfig: World = World(),
    val dungeonsConfig: Dungeons = Dungeons(),
    val monolithsConfig: Monoliths = Monoliths(),
    val limboConfig: Limbo = Limbo(),
    val graphicsConfig: Graphics = Graphics(),
    val doorsConfig: Doors = Doors(),
    val decayConfig: Decay = Decay()
) {
    data class General(
        var teleportOffset: Double = 0.0,
        var riftBoundingBoxInCreative: Boolean = false,
        var riftCloseSpeed: Double = 0.1,
        var riftGrowthSpeed: Double = 1.0,
        var enableRiftDecay: Boolean = true,
        var depthSpreadFactor: Int = 20,
        var endermanSpawnChance: Double = 0.00005,
        var endermanAggressiveChance: Double = 0.5,
        var enableDebugMessages: Boolean = false
    ) {

        companion object {
            val codec: MapCodec<General> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                Codec.DOUBLE.fieldOf("teleportOffset").forGetter(General::teleportOffset),
                Codec.BOOL.fieldOf("riftBoundingBoxInCreative").forGetter(General::riftBoundingBoxInCreative),
                Codec.DOUBLE.fieldOf("riftCloseSpeed").forGetter(General::riftCloseSpeed),
                Codec.DOUBLE.fieldOf("riftGrowthSpeed").forGetter(General::riftGrowthSpeed),
                Codec.BOOL.fieldOf("enableRiftDecay").forGetter(General::enableRiftDecay),
                Codec.INT.fieldOf("depthSpreadFactor").forGetter(General::depthSpreadFactor),
                Codec.DOUBLE.fieldOf("endermanSpawnChance").forGetter(General::endermanSpawnChance),
                Codec.DOUBLE.fieldOf("endermanAggressiveChance").forGetter(General::endermanAggressiveChance),
                Codec.BOOL.fieldOf("enableDebugMessages").forGetter(General::enableDebugMessages)
            ).apply(builder, ::General) }
        }
    }

    class Doors {
        @JvmField
        var closeDoorBehind: Boolean = true
        @JvmField
        var doorList: DoorList = DoorList()
        @JvmField
        var placeRiftsInCreativeMode: Boolean = true

        class DoorList {
            @JvmField
            var mode: Mode = Mode.DISABLE
            @JvmField
            var doors = mutableListOf<ResourceLocation>()

            enum class Mode(val key: String) {
                ENABLE("dimdoors.mode.enable"),
                DISABLE("dimdoors.mode.disable");

                val enabled get() = this == ENABLE
            }

            companion object {
                val codec: MapCodec<DoorList> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                    Codec.STRING.xmap(Mode::valueOf, Mode::name).fieldOf("mode").forGetter(DoorList::mode),
                    ResourceLocation.CODEC.mutableList().fieldOf("doors").forGetter(DoorList::doors)
                ).apply(builder) { mode, doors -> DoorList().also { it.mode = mode; it.doors = doors } } }
            }
        }

        fun isAllowed(id: ResourceLocation): Boolean = doorList.run { doors.contains(id) == mode.enabled }

        companion object {
            val codec: MapCodec<Doors> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                Codec.BOOL.fieldOf("closeDoorBehind").forGetter(Doors::closeDoorBehind),
                DoorList.codec.codec().fieldOf("doorList").forGetter(Doors::doorList),
                Codec.BOOL.fieldOf("placeRiftsInCreativeMode").forGetter(Doors::placeRiftsInCreativeMode)
            ).apply(builder) { closeDoorBehind, doorList, placeRiftsInCreativeMode -> Doors().also {
                it.closeDoorBehind = closeDoorBehind
                it.doorList = doorList
                it.placeRiftsInCreativeMode = placeRiftsInCreativeMode
            } } }
        }
    }

    data class Pockets(
        var pocketGridSize: Int = 32,
        var maxPocketSize: Int = 15,
        var privatePocketSize: Int = 2,
        var publicPocketSize: Int = 1,
        var blocksColoredPerDye: Int = 100
    ) {
        companion object {
            val codec: MapCodec<Pockets> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                Codec.INT.fieldOf("pocketGridSize").forGetter(Pockets::pocketGridSize),
                Codec.INT.fieldOf("maxPocketSize").forGetter(Pockets::maxPocketSize),
                Codec.INT.fieldOf("privatePocketSize").forGetter(Pockets::privatePocketSize),
                Codec.INT.fieldOf("publicPocketSize").forGetter(Pockets::publicPocketSize),
                Codec.INT.fieldOf("blocksColoredPerDye").forGetter(Pockets::blocksColoredPerDye)
            ).apply(builder, ::Pockets) }
        }
    }

    data class World(
        @JvmField var clusterGenChance: Double = 20000.0,
        @JvmField var clusterDimBlacklist: MutableList<String> = mutableListOf(),
        @JvmField var gatewayDimBlacklist: MutableList<String> = mutableListOf()
    ) {
        companion object {
            val codec: MapCodec<World> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                Codec.DOUBLE.fieldOf("clusterGenChance").forGetter(World::clusterGenChance),
                Codec.STRING.mutableList().fieldOf("clusterDimBlacklist").forGetter(World::clusterDimBlacklist),
                Codec.STRING.mutableList().fieldOf("gatewayDimBlacklist").forGetter(World::gatewayDimBlacklist)
            ).apply(builder, ::World) }
        }
    }

    data class Dungeons(
        @JvmField var maxDungeonDepth: Int = 50
    ) {
        companion object {
            val codec: MapCodec<Dungeons> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                Codec.INT.fieldOf("maxDungeonDepth").forGetter(Dungeons::maxDungeonDepth)
            ).apply(builder, ::Dungeons) }
        }
    }

    data class Monoliths(
        @JvmField var dangerousLimboMonoliths: Boolean = false,
        @JvmField var monolithTeleportation: Boolean = true
    ) {
        companion object {
            val codec: MapCodec<Monoliths> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                Codec.BOOL.fieldOf("dangerousLimboMonoliths").forGetter(Monoliths::dangerousLimboMonoliths),
                Codec.BOOL.fieldOf("monolithTeleportation").forGetter(Monoliths::monolithTeleportation)
            ).apply(builder, ::Monoliths) }
        }
    }

    class Limbo {
        @JvmField
        var genericDeathMessages: Boolean = false
        @JvmField
        val worldsLeadingToLimbo: WorldList = WorldList()
        @JvmField
        var hardcoreLimbo: Boolean = false

        @JvmField
        var limboReturnDistanceMax: Int = 200
        @JvmField
        var limboReturnDistanceMin: Int = 100

        @JvmField
        var decaySurroundings: Boolean = false

        @JvmField
        var tryPlayerBedSpawn: Boolean = false
        @JvmField
        var defaultToWorldSpawn: Boolean = true

        @JvmField
        var limboBlocksCorruptingExitWorldAmount: Float = 5f
        @JvmField
        var escapeTargetWorld: ResourceKey<Level> = Level.OVERWORLD

        fun shouldUseLimbo(level: ResourceKey<Level>): Boolean =
            worldsLeadingToLimbo.blacklist != worldsLeadingToLimbo.list.contains(level)

        data class WorldList @JvmOverloads constructor(
            @JvmField var list: MutableList<ResourceKey<Level>> = mutableListOf<ResourceKey<Level>>(),
            @JvmField var blacklist: Boolean = false
        ) {
            companion object {
                val codec: MapCodec<WorldList> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                    Level.RESOURCE_KEY_CODEC.listOf().xmap({ it.toMutableList() }, { it }).fieldOf("list").forGetter(WorldList::list),
                    Codec.BOOL.fieldOf("blacklist").forGetter(WorldList::blacklist)
                ).apply(builder, ::WorldList) }
            }
        }

        companion object {
            val codec: MapCodec<Limbo> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                Codec.BOOL.fieldOf("genericDeathMessages").forGetter(Limbo::genericDeathMessages),
                WorldList.codec.codec().fieldOf("worldsLeadingToLimbo").forGetter(Limbo::worldsLeadingToLimbo),
                Codec.BOOL.fieldOf("hardcoreLimbo").forGetter(Limbo::hardcoreLimbo),
                Codec.INT.fieldOf("limboReturnDistanceMax").forGetter(Limbo::limboReturnDistanceMax),
                Codec.INT.fieldOf("limboReturnDistanceMin").forGetter(Limbo::limboReturnDistanceMin),
                Codec.BOOL.fieldOf("decaySurroundings").forGetter(Limbo::decaySurroundings),
                Codec.BOOL.fieldOf("tryPlayerBedSpawn").forGetter(Limbo::tryPlayerBedSpawn),
                Codec.BOOL.fieldOf("defaultToWorldSpawn").forGetter(Limbo::defaultToWorldSpawn),
                Codec.FLOAT.fieldOf("limboBlocksCorruptingExitWorldAmount").forGetter(Limbo::limboBlocksCorruptingExitWorldAmount),
                Level.RESOURCE_KEY_CODEC.fieldOf("escapeTargetWorld").forGetter(Limbo::escapeTargetWorld)
            ).apply(builder) { genericDeathMessages, worldsLeadingToLimbo, hardcoreLimbo, limboReturnDistanceMax, limboReturnDistanceMin,
                               decaySurroundings, tryPlayerBedSpawn, defaultToWorldSpawn, limboBlocksCorruptingExitWorldAmount, escapeTargetWorld ->
                Limbo().also {
                    it.genericDeathMessages = genericDeathMessages
                    it.worldsLeadingToLimbo.list = worldsLeadingToLimbo.list
                    it.worldsLeadingToLimbo.blacklist = worldsLeadingToLimbo.blacklist
                    it.hardcoreLimbo = hardcoreLimbo
                    it.limboReturnDistanceMax = limboReturnDistanceMax
                    it.limboReturnDistanceMin = limboReturnDistanceMin
                    it.decaySurroundings = decaySurroundings
                    it.tryPlayerBedSpawn = tryPlayerBedSpawn
                    it.defaultToWorldSpawn = defaultToWorldSpawn
                    it.limboBlocksCorruptingExitWorldAmount = limboBlocksCorruptingExitWorldAmount
                    it.escapeTargetWorld = escapeTargetWorld
                }
            } }
        }
    }

    data class Decay(
        @JvmField var decaySpreadChance: Double = 1.0,
        @JvmField var decayDelay: Int = 40,
        @JvmField var decaysIntoAir: Boolean = true
    ) {
        companion object {
            val codec: MapCodec<Decay> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                Codec.DOUBLE.fieldOf("decaySpreadChance").forGetter(Decay::decaySpreadChance),
                Codec.INT.fieldOf("decayDelay").forGetter(Decay::decayDelay),
                Codec.BOOL.fieldOf("decaysIntoAir").forGetter(Decay::decaysIntoAir)
            ).apply(builder, ::Decay) }
        }
    }

    data class Graphics(
        @JvmField var showRiftCore: Boolean = false,
        @JvmField var highlightRiftCoreFor: Int = 15000 / 1000,
        @JvmField var riftSize: Double = 1.0,
        @JvmField var riftJitter: Double = 1.0
    ) {
        companion object {
            val codec: MapCodec<Graphics> = RecordCodecBuilder.mapCodec { builder -> builder.group(
                Codec.BOOL.fieldOf("showRiftCore").forGetter(Graphics::showRiftCore),
                Codec.INT.fieldOf("highlightRiftCoreFor").forGetter(Graphics::highlightRiftCoreFor),
                Codec.DOUBLE.fieldOf("riftSize").forGetter(Graphics::riftSize),
                Codec.DOUBLE.fieldOf("riftJitter").forGetter(Graphics::riftJitter)
            ).apply(builder, ::Graphics) }
        }
    }

    companion object {
        val codec = RecordCodecBuilder.create<ModConfig> { instance -> instance.group(
            General.codec.codec().fieldOf("general").forGetter(ModConfig::generalConfig),
            Pockets.codec.codec().fieldOf("pockets").forGetter(ModConfig::pocketsConfig),
            World.codec.codec().fieldOf("world").forGetter(ModConfig::worldConfig),
            Dungeons.codec.codec().fieldOf("dungeons").forGetter(ModConfig::dungeonsConfig),
            Monoliths.codec.codec().fieldOf("monoliths").forGetter(ModConfig::monolithsConfig),
            Limbo.codec.codec().fieldOf("limbo").forGetter(ModConfig::limboConfig),
            Graphics.codec.codec().fieldOf("graphics").forGetter(ModConfig::graphicsConfig),
            Doors.codec.codec().fieldOf("doors").forGetter(ModConfig::doorsConfig),
            Decay.codec.codec().fieldOf("decay").forGetter(ModConfig::decayConfig)
        ).apply(instance, ::ModConfig) }

        val type = ConfigType<ModConfig>(codec, ::ModConfig)
    }
}
