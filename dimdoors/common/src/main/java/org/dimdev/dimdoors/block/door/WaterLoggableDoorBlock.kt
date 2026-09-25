package org.dimdev.dimdoors.block.door

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.*
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids

abstract class WaterLoggableDoorBlock(
    settings: Properties,
    blockSetType: BlockSetType,
    addWaterlog: Boolean
) : DoorBlock(blockSetType, settings), SimpleWaterloggedBlock {
    private val addWaterlog: Boolean = addWaterlog && !this.stateDefinition.properties.contains(WATERLOGGED)

    init {
        val builder = StateDefinition.Builder<Block, BlockState>(this)
        this.createBlockStateDefinition(builder)
        this.stateDefinition = builder.create(Block::defaultBlockState, ::BlockState)

        registerDefaultState(
            this.getStateDefinition().any().setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(HINGE, DoorHingeSide.LEFT)
                .setValue(POWERED, false)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(WATERLOGGED, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        if (addWaterlog) builder.add(WATERLOGGED)
    }


    //    @Override
    //    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
    //    InteractionResult result = super.useWithoutItem(state, world, pos, player, hit);
    //    if (result.consumesAction()) {
    //        world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
    //    }
    //    return result;
    //    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        val up = pos.above()
        level.setBlock(
            up,
            state.setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(WATERLOGGED, level.isWaterAt(up)),
            3
        )
    }

    //    @Override
    //    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
    //    boolean bl = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
    //    super.neighborChanged(state, world, pos, block, fromPos, notify);
    //    if (bl && !world.isClientSide && state.getValue(WATERLOGGED)) {
    //        world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
    //    }
    //    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        val fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos())
        val bl = fluidState.getType() === Fluids.WATER
        var state = super.getStateForPlacement(ctx)
        if (state != null) state = state.setValue<Boolean?, Boolean?>(WATERLOGGED, bl)

        return state
    }

    override fun updateShape(
        blockState: BlockState,
        direction: Direction,
        blockState2: BlockState,
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockPos2: BlockPos
    ): BlockState {
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor))
        }

        var result = super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2)
        if (result.hasProperty(WATERLOGGED) && blockState.hasProperty(WATERLOGGED)) {
            result = result.setValue(WATERLOGGED, blockState.getValue(WATERLOGGED))
        }
        return result
    }

    override fun getFluidState(blockState: BlockState): FluidState = if (blockState.getValue(WATERLOGGED) as Boolean) Fluids.WATER.getSource(false) else super.getFluidState(blockState)

    override fun playerWillDestroy(world: Level, pos: BlockPos, state: BlockState, player: Player) = super.playerWillDestroy(world, pos, state, player)

    companion object {
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
    }
}
