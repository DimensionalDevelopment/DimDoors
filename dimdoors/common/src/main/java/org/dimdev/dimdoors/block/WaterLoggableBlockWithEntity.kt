package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult

abstract class WaterLoggableBlockWithEntity protected constructor(settings: Properties) : BaseEntityBlock(settings),
    SimpleWaterloggedBlock {
    init {
        registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(WATERLOGGED)
    }

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        val result = super.useWithoutItem(state, world, pos, player, blockHitResult)
        if (result.consumesAction()) world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))
        return result
    }

    public override fun neighborChanged(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        notify: Boolean
    ) {
        super.neighborChanged(state, world, pos, block, fromPos, notify)
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))
        }
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        val water = ctx.level.getFluidState(ctx.clickedPos).type === Fluids.WATER
        val state = super.getStateForPlacement(ctx) ?: return null
        if (water) return state.setValue(WATERLOGGED, true)
        return state
    }

    public override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.getValue(WATERLOGGED)) world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))

        val newState = super.updateShape(state, direction, neighborState, world, pos, neighborPos)
        return if (newState.isAir && state.fluidState.type === Fluids.WATER) Blocks.WATER.defaultBlockState() else newState
    }

    public override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    companion object {
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
    }
}
