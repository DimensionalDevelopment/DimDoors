package org.dimdev.dimdoors.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult

abstract class CrucibleOfFrayBlock(properties: BlockBehaviour.Properties): BaseEntityBlock(properties) {

    protected abstract override fun codec(): MapCodec<out BaseEntityBlock>

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if(level.isClientSide) {
            return InteractionResult.SUCCESS
        } else {
            this.openContainer(level, pos, player)
            return InteractionResult.CONSUME
        }
    }

    protected abstract fun openContainer(level: Level, pos: BlockPos, player: Player)

    override fun getStateForPlacement(context: BlockPlaceContext) = this.defaultBlockState().setValue(FACING, context.horizontalDirection)

    protected override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if(!state.`is`(newState.block)) {
            val blockEntity = level.getBlockEntity(pos)
            if(blockEntity is CrucibleOfFrayBlockEntity)
        }
    }

    companion object {
        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
        val LIT = BlockStateProperties.LIT
    }


}