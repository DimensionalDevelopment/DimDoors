package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.entity.Rift
import java.util.function.Supplier

interface RiftProvider<T> : EntityBlock, RiftVariantProvider, PerservesBlockEntity where T : BlockEntity, T : Rift {
    fun getRift(world: Level, pos: BlockPos, state: BlockState): T? {
        val rifPos = getRiftPos(world, pos, state)

        return world.getBlockEntity(rifPos, this.riftBlockEnityType)
            .orElseGet(Supplier {
                DimensionalDoors.LOGGER.warn("{} at {} in world {} contained no rift.", providerType(), rifPos, world)
                null
            })
    }

    fun getRiftPos(world: Level, pos: BlockPos, state: BlockState): BlockPos = pos

    override fun convertToRiftProvider(world: ServerLevel, pos: BlockPos, state: BlockState): Rift? {
        var pos = pos
        pos = getRiftPos(world, pos, state)
        return getRift(world, pos, state)
    }

    fun providerType(): String = "Rift Block"

    fun isTall(cachedState: BlockState): Boolean = false

    fun stateContainsRift(oldState: BlockState): Boolean = true

    override fun isCompatible(oldState: BlockState): Boolean {
        return oldState.block.castOrNull<RiftProvider<*>>()?.stateContainsRift(oldState) == true
    }

    override fun attemptTransfer(blockEntity: BlockEntity, blockEntityToBeTransfered: BlockEntity) {
        if (blockEntity is Rift && blockEntityToBeTransfered is Rift) {
            blockEntity.copyFrom(blockEntityToBeTransfered)
        }
    }

    val riftBlockEnityType: BlockEntityType<T>

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return this.riftBlockEnityType.create(pos, state)
    }

    companion object {
        fun <R> tickRift(
            level: Level,
            blockPos: BlockPos,
            state: BlockState,
            rift: R?
        ) where R : BlockEntity, R : Rift {
            rift!!.tick(level, blockPos, state)
        }
    }
}
