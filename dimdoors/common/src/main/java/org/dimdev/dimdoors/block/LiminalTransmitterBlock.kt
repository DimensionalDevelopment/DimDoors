package org.dimdev.dimdoors.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.block.entity.RiftBlockEntity
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.rift.targets.Targets

class LiminalTransmitterBlock(properties: Properties) : WaterLoggableBlockWithEntity(properties), RiftProvider<RiftBlockEntity.Impl> {
    override fun codec(): MapCodec<out BaseEntityBlock> {
        return CODEC
    }

    override val riftBlockEnityType get() = ModBlockEntityTypes.GENERIC_RIFT

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    init {
        registerDefaultState(defaultBlockState().setValue(POWERED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(POWERED)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        if (!ctx.level.getBlockState(ctx.clickedPos).`is`(ModBlocks.DETACHED_RIFT)) {
            return null
        }

        val state = if (ctx.player == null || !ctx.player!!.isShiftKeyDown) defaultBlockState() else super.getStateForPlacement(ctx) ?: return null

        return state.setValue(POWERED, ctx.level.hasNeighborSignal(ctx.clickedPos))
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return canSupportRigidBlock(level, pos.below())
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (direction == Direction.DOWN && !state.canSurvive(world, pos)) {
            dropResources(state, world, pos, world.getBlockEntity(pos))
            return ModBlocks.DETACHED_RIFT.defaultBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED))
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
        super.onPlace(state, level, pos, oldState, movedByPiston)
        if (!level.isClientSide && !oldState.`is`(this)) level.scheduleTick(pos, this, 1)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean) {
        if (!level.isClientSide && !newState.`is`(this)) getRift(level, pos, state)?.let { attemptRedstoneTransmission(0, it) }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun neighborChanged(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        notify: Boolean
    ) {
        super.neighborChanged(state, world, pos, block, fromPos, notify)
        if (!world.isClientSide && !world.blockTicks.hasScheduledTick(pos, this)) world.scheduleTick(pos, this, 1)
    }

    override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        val signal = level.getBestNeighborSignal(pos)
        val powered = signal > 0
        if (state.getValue(POWERED) != powered) level.setBlock(pos, state.setValue(POWERED, powered), UPDATE_CLIENTS)

        val rift = getRift(level, pos, state) ?: return
        attemptRedstoneTransmission(signal, rift)
    }

    companion object {
        val CODEC: MapCodec<LiminalTransmitterBlock> = simpleCodec(::LiminalTransmitterBlock)

        val POWERED: BooleanProperty = BlockStateProperties.POWERED

        val SHAPE = Shapes.create(0.0, 0.0, 0.0, 1.0, 2.0/16.0, 1.0) + Shapes.create(2.0/16.0, 2.0/16.0, 2.0/16.0, 14.0/16.0, 4.0/16.0, 14.0/16.0)

        fun attemptRedstoneTransmission(strength: Int, rift: Rift): Boolean {
            rift.isStateDirty = false

            // Attempt a teleport
            try {
                val target = rift.target
                val location = target.castOrNull<LocationProvider>()?.providedLocation

                val redstone = target.`as`(Targets.REDSTONE)

                return redstone?.recieveSignal(strength, location) ?: false
            } catch (e: Exception) {
                DimensionalDoors.LOGGER.error("Redstone transmission from {} failed", rift.riftBlockPos, e)
                return false
            }


        }


    }
}

operator fun VoxelShape.plus(shape: VoxelShape): VoxelShape = Shapes.join(this, shape, BooleanOp.AND)
