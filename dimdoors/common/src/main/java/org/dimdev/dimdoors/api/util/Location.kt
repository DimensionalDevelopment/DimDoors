package org.dimdev.dimdoors.api.util

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import org.dimdev.dimdoors.DimensionalDoors.Companion.server
import org.dimdev.dimdoors.rift.targets.RiftReference
import org.dimdev.dimdoors.rift.targets.VirtualTarget

open class Location(val worldId: ResourceKey<Level>, val blockPos: BlockPos) {
    private constructor(world: ResourceKey<Level>, x: Int, y: Int, z: Int) : this(world, BlockPos(x, y, z))

    val x: Int get() = this.blockPos.x

    val y: Int get() = this.blockPos.y

    val z: Int get() = this.blockPos.z

    val blockState: BlockState
        get() = this.world.getBlockState(this.blockPos)

    val fluidState: FluidState
        get() = this.world.getFluidState(this.blockPos)

    val blockEntity: BlockEntity?
        get() = this.world.getBlockEntity(this.blockPos)

    val biome: Holder<Biome>
        get() = this.world.getBiome(this.blockPos)

    override fun equals(other: Any?): Boolean {
        return other is Location &&
                other.worldId == this.worldId &&
                other.blockPos == this.blockPos
    }

    override fun hashCode(): Int = this.worldId.hashCode() * 31 + this.blockPos.hashCode()

    val world: ServerLevel get() { return server.getLevel(this.worldId)!! }

    val target: VirtualTarget<*> = RiftReference(this)

    fun asTarget(): VirtualTarget<*> = RiftReference(this)

    companion object {
        @JvmField
        val CODEC: Codec<Location> = RecordCodecBuilder.create { instance -> instance.group(
                    Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(Location::worldId),
                    BlockPos.CODEC.fieldOf("pos").forGetter(Location::blockPos)
                ).apply(instance, ::Location)
            }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Location> = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), Location::worldId, BlockPos.STREAM_CODEC, Location::blockPos, ::Location)

        fun ofWorld(level: ServerLevel, x: Int, y: Int, z: Int): Location {
            return ofWorld(level, BlockPos(x, y, z))
        }

        @JvmStatic
        fun ofWorld(level: ServerLevel, pos: BlockPos): Location {
            return Location(level.dimension(), pos)
        }

        @JvmStatic
        fun getHeightmapPosSafe(level: ServerLevel, x: Int, z: Int): BlockPos {
            val mutablePos = BlockPos.MutableBlockPos(x, level.maxBuildHeight, z)
            while (mutablePos.y > level.minBuildHeight && (!level.getBlockState(mutablePos).isSolid && level.getFluidState(mutablePos).isEmpty)) mutablePos.move(Direction.DOWN)

            return mutablePos.move(Direction.UP)
        }
    }
}
