package org.dimdev.dimdoors.world.pocket

import com.google.common.base.MoreObjects
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.Heightmap
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.world.ModDimensions

data class VirtualLocation(val world: ResourceKey<Level>, val x: Int, val z: Int, val depth: Int) {

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("world", this.world)
            .add("x", this.x)
            .add("z", this.z)
            .add("depth", this.depth)
            .toString()
    }

    companion object {
        val CODEC = RecordCodecBuilder.create { instance ->
                instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(VirtualLocation::world),
                    Codec.INT.fieldOf("x").forGetter(VirtualLocation::x),
                    Codec.INT.fieldOf("z").forGetter(VirtualLocation::z),
                    Codec.INT.fieldOf("depth").forGetter(VirtualLocation::depth)
                ).apply(instance, ::VirtualLocation)
            }

        @JvmStatic
        fun fromLocation(provider: LocationProvider): VirtualLocation {
            val location = provider.providedLocation!!

            return when {
                ModDimensions.isPocketDimension(location.worldId) -> {
                    val pocket = instance.getPocketDirectory(location.worldId).getPocketAt(location.blockPos)
                        ?: return VirtualLocation(Level.OVERWORLD, location.x, location.z, baseDepth(Level.OVERWORLD))
                    val entrance = instance.getPocketEntrance(pocket)?.blockPos ?: location.blockPos
                    val scale = pocket.virtualLocation.depth + 1
                    pocket.virtualLocation.let { it.copy(x = it.x + (location.x - entrance.x) * scale, z = it.z + (location.z - entrance.z) * scale) }
                }
                ModDimensions.isLimboDimension(location.world) -> VirtualLocation(location.worldId, location.x, location.z, DimensionalDoors.config.dungeonsConfig.maxDungeonDepth) // TODO: convert to interface on worldprovider
                else -> VirtualLocation(location.worldId, location.x, location.z, baseDepth(location.worldId))
            }
        }

        fun baseDepth(world: ResourceKey<Level>): Int = if(world == Level.NETHER) 8 else 1

        @JvmStatic
        fun getTopPos(world: Level, x: Int, z: Int): BlockPos {
            val topHeight = world.getChunk(
                SectionPos.blockToSectionCoord(x),
                SectionPos.blockToSectionCoord(z)
            ).getHeight(Heightmap.Types.MOTION_BLOCKING, x, z)
            return BlockPos(x, topHeight, z)
        }
    }
}
