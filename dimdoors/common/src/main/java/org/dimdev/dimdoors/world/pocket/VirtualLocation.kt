package org.dimdev.dimdoors.world.pocket

import com.google.common.base.MoreObjects
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.Heightmap
import org.dimdev.dimdoors.DimensionalDoors.Companion.getConfig
import org.dimdev.dimdoors.DimensionalDoors.Companion.server
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.world.ModDimensions

data class VirtualLocation(val world: ResourceKey<Level>, val x: Int, val z: Int, val depth: Int) {

    fun projectToWorld(acceptLimbo: Boolean): Location {
        var world = server.getLevel(this.world)!!

        if (!acceptLimbo && ModDimensions.isLimboDimension(world)) {
            world = world.server.overworld()
        }

        val spread = (getConfig().generalConfig.depthSpreadFactor * this.depth).toFloat()
        val newX = (this.x + spread * 2 * (Math.random() - 0.5)).toInt()
        val newZ = (this.z + spread * 2 * (Math.random() - 0.5)).toInt()
        //BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(newX, 1, newZ));
        val pos: BlockPos = getTopPos(world, newX, newZ).above()
        return Location.ofWorld(world, pos)
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("world", this.world)
            .add("x", this.x)
            .add("z", this.z)
            .add("depth", this.depth)
            .toString()
    }

    companion object {
        var CODEC = RecordCodecBuilder.create { instance ->
                instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(VirtualLocation::world),
                    Codec.INT.fieldOf("x").forGetter(VirtualLocation::x),
                    Codec.INT.fieldOf("z").forGetter(VirtualLocation::z),
                    Codec.INT.fieldOf("depth").forGetter(VirtualLocation::depth)
                ).apply(instance, ::VirtualLocation)
            }

        @JvmStatic
        fun fromLocation(location: Location): VirtualLocation = when {
                ModDimensions.isPocketDimension(location.worldId) -> instance.getPocketDirectory(location.worldId).getPocketAt(location.blockPos)?.virtualLocation
                ModDimensions.isLimboDimension(location.world) -> VirtualLocation(location.worldId, location.x, location.z, getConfig().dungeonsConfig.maxDungeonDepth) // TODO: convert to interface on worldprovider
                else -> VirtualLocation(location.worldId, location.x, location.y, 5) // TODO: nether coordinate transform
            }?.let { VirtualLocation(location.worldId, location.x, location.z, it.depth) } ?: VirtualLocation(Level.OVERWORLD, location.x, location.z, 5)

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
