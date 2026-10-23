package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.block.entity.DriftwoodChestBlockEntity
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes

class DriftwoodChestBlock(properties: Properties) : ChestBlock(properties, { ModBlockEntityTypes.DRIFTWOOD_CHEST }) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = DriftwoodChestBlockEntity(pos, state)
}