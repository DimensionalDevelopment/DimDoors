package org.dimdev.dimdoors

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.util.ConfigType

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
    )

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
        }

        fun isAllowed(id: ResourceLocation): Boolean = doorList.run { doors.contains(id) == mode.enabled }
    }

    data class Pockets(
        var pocketGridSize: Int = 32,
        var maxPocketSize: Int = 15,
        var privatePocketSize: Int = 2,
        var publicPocketSize: Int = 1,
        var blocksColoredPerDye: Int = 100
    )

    data class World(
        @JvmField var clusterGenChance: Double = 20000.0,
        @JvmField var clusterDimBlacklist: MutableList<String> = mutableListOf<String>(),
        @JvmField var gatewayDimBlacklist: MutableList<String> = mutableListOf<String>()
    )

    data class Dungeons(
        @JvmField var maxDungeonDepth: Int = 50
    )

    data class Monoliths(
        @JvmField var dangerousLimboMonoliths: Boolean = false,
        @JvmField var monolithTeleportation: Boolean = true
    )

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
        )
    }

    data class Decay(
        @JvmField var decaySpreadChance: Double = 1.0,
        @JvmField var decayDelay: Int = 40,
        @JvmField var decaysIntoAir: Boolean = true
    )

    data class Graphics(
        @JvmField var showRiftCore: Boolean = false,
        @JvmField var highlightRiftCoreFor: Int = 15000 / 1000,
        @JvmField var riftSize: Double = 1.0,
        @JvmField var riftJitter: Double = 1.0
    )

    companion object {
        val codec = RecordCodecBuilder.create<ModConfig> { instance -> instance.group(

        ) }
        val type = ConfigType<ModConfig>(codec, ::ModConfig)
    }
}