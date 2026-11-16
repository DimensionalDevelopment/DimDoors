package org.dimdev.dimdoors.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.state.BlockState

class DriftwoodChestBlockEntity(pos: BlockPos, blockState: BlockState): ChestBlockEntity(ModBlockEntityTypes.DRIFTWOOD_CHEST, pos,blockState)