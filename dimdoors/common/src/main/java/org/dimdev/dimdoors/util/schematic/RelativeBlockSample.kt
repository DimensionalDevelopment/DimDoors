package org.dimdev.dimdoors.util.schematic

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.CommandBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.CommandBlockEntity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.ext.minusAssign
import org.dimdev.dimcore.api.ext.set
import org.dimdev.dimdoors.entity.mask.MaskEntity

class RelativeBlockSample(@JvmField val schematic: Schematic) {
    private val blockEntities: Map<BlockPos, CompoundTag> = schematic.blocks.blockEntities.mapNotNull { nbt ->
        nbt.getIntArray("Pos").takeIf { it.size >= 3 }?.let { BlockPos(it[0], it[1], it[2]) to toVanilla(nbt) }
    }.toMap()
    private val entityContainer: BiMap<CompoundTag, Vec3> = HashBiMap.create()

    init {
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

        val states = schematic.blocks.palette.inverse()
        val data = schematic.blocks.data
        val width = schematic.width.toInt()
        val length = schematic.length.toInt()
        val commandBlocks = mutableListOf<BlockPos>()

        runCatching {
            for (index in data.indices) {
                val state = states.getValue(data[index])
                val pos = BlockPos(index % width, index / (width * length), index / width % length)
                val actualPos = origin.offset(pos)

                world.setBlock(actualPos, state, 0)
                blockEntities[pos]?.let { BlockEntity.loadStatic(actualPos, state, it, provider) }?.let(world::setBlockEntity)
                if (state.block is CommandBlock) commandBlocks += actualPos
            }
        }.also { shouldUpdate = true }

        this.initializePlacedCommandBlocks(commandBlocks, world)


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

    private fun initializePlacedCommandBlocks(positions: List<BlockPos>, world: ServerLevel) {
        positions.forEach { actualPos ->
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
