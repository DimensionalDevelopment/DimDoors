package org.dimdev.dimdoors.block

import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

interface PerservesBlockEntity {
    fun isCompatible(oldState: BlockState): Boolean

    fun attemptTransfer(blockEntity: BlockEntity, blockEntityToBeTransfered: BlockEntity)
}
