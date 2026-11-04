package org.dimdev.dimdoors.util.schematic

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.Maps
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.CommandBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.CommandBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.ext.minusAssign
import org.dimdev.dimcore.api.ext.set
import org.dimdev.dimdoors.entity.mask.MaskEntity

class RelativeBlockSample(@JvmField val schematic: Schematic) {
    private val blockDataContainer: MutableMap<BlockPos, BlockData> = Maps.newHashMap()
    private val entityContainer: BiMap<CompoundTag, Vec3> = HashBiMap.create()

    private class BlockData {
        var state: BlockState? = null
        var blockEntity: CompoundTag? = null
        var biome: ResourceKey<Biome>? = null
    }

    init {
        val blockData = schematic.blocks
        val biomeData = schematic.biomes
        val width = schematic.width.toInt()
        val height = schematic.height.toInt()
        val length = schematic.length.toInt()

        fun <T> Schematic.DataBlock<T>.get(x: Int, y: Int, z: Int): T? {
            val index = x + z * width + y * width * length

            if(index >= data.size) return null

            return palette.inverse().getValue(data[x + z * width + y * width * length])
        }

        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length) {

                    val data = blockDataContainer.computeIfAbsent(BlockPos(x, y, z)) { BlockData() }
                    data.state = blockData.get(x, y, z)
                    data.biome = biomeData.get(x, y, z)
                }
            }
        }

        for (blockEntityNbt in schematic.blocks.blockEntities) {
            val arr = blockEntityNbt.getIntArray("Pos")
            if (arr.size < 3) continue
            val position = BlockPos(arr[0], arr[1], arr[2])

            blockDataContainer.computeIfAbsent(position) { BlockData() }.blockEntity = toVanilla(blockEntityNbt)
        }

        for (entityNbt in schematic.entities) {
            val doubles = entityNbt.getList("Pos", Tag.TAG_DOUBLE.toInt())
            this.entityContainer[toVanilla(entityNbt)] =
                Vec3(doubles.getDouble(0), doubles.getDouble(1), doubles.getDouble(2)).subtract(
                    Vec3.atLowerCornerOf(schematic.offset)
                )
        }
    }

    fun place(origin: BlockPos, world: ServerLevel, biomes: Boolean, provider: HolderLookup.Provider) {
        shouldUpdate = false

        //TODO: in 26.4, investigate how they do block precise biome setting. Use it to implment schematic biome placement.

        runCatching {
            this.blockDataContainer.forEach { (pos, data) ->
                val state = data.state ?: return@forEach
                val actualPos = origin.offset(pos)

                world.setBlock(actualPos, state, 0)
                data.blockEntity?.let { BlockEntity.loadStatic(actualPos, state, it, provider) }?.let(world::setBlockEntity)
            }
        }.also { shouldUpdate = true }

        this.initializePlacedCommandBlocks(origin, world)


        for ((nbt, value) in this.entityContainer) {
            val doubles = nbt.getList("Pos", Tag.TAG_DOUBLE.toInt())
            val vec = value.add(origin.x.toDouble(), origin.y.toDouble(), origin.z.toDouble())

            doubles[0] = vec.x
            doubles[1] = vec.y
            doubles[2] = vec.z

            nbt["Pos"] = doubles

            nbt -= "UUID"

            EntityType.create(nbt, world.level).ifPresent {
                if (it is MaskEntity) it.frozen = false
                world.addFreshEntity(it)
            }
        }
    }

    private fun initializePlacedCommandBlocks(origin: BlockPos, world: ServerLevel) {
        this.blockDataContainer.forEach { (pos, data) ->
            if (data.state?.block !is CommandBlock) {
                return@forEach
            }

            val actualPos = origin.offset(pos)
            val commandBlock = world.getBlockEntity(actualPos) as? CommandBlockEntity ?: return@forEach

            val state = world.getBlockState(actualPos)
            val block = state.block as? CommandBlock ?: return@forEach

            val powered = world.hasNeighborSignal(actualPos)
            commandBlock.isPowered = powered

            if (commandBlock.mode == CommandBlockEntity.Mode.SEQUENCE) {
                return@forEach
            }

            if (powered || commandBlock.isAutomatic) {
                commandBlock.markConditionMet()
                world.scheduleTick(actualPos, block, 1)
            }
        }
    }

    companion object {
        @JvmField
        var shouldUpdate = true

        private fun toVanilla(sponge: CompoundTag): CompoundTag {
            val vanilla = sponge.getCompound("Data").copy()
            vanilla.putString("id", sponge.getString("Id"))
            sponge["Pos"]?.let { vanilla["Pos"] = it.copy() }

            return vanilla
        }
    }
}