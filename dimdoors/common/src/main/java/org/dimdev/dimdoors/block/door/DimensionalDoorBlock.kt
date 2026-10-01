package org.dimdev.dimdoors.block.door

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.dimdev.dimdoors.DimensionalDoors.Companion.getDimensionalDoorBlockRegistrar
import org.dimdev.dimdoors.api.util.horizontalFacing
import org.dimdev.dimdoors.api.util.math.MathUtil.eulerAngle
import org.dimdev.dimdoors.api.util.math.MathUtil.plus
import org.dimdev.dimdoors.api.util.math.MathUtil.times
import org.dimdev.dimdoors.api.util.math.inverseRotateLocal
import org.dimdev.dimdoors.api.util.math.inverseTranslateLocal
import org.dimdev.dimdoors.block.DimensionalPortalBlock.Companion.checkType
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimdoors.block.TraversableRiftBlock
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.rift.RiftUtils
import org.dimdev.dimdoors.rift.RiftUtils.PortalPlane.Companion.ofDoor
import org.joml.Matrix4d
import org.joml.Matrix4dc

abstract class DimensionalDoorBlock<T : EntranceRiftBlockEntity<*>>(
    settings: Properties,
    blockSetType: BlockSetType,
    addWaterlog: Boolean
) : WaterLoggableDoorBlock(settings.pushReaction(PushReaction.BLOCK), blockSetType, addWaterlog), TraversableRiftBlock<T> {
    override fun entityInside(state: BlockState, world: Level, pos: BlockPos, entity: Entity) = super<TraversableRiftBlock>.entityInside(state, world, pos, entity)

    override fun onBlockExploded(state: BlockState, level: Level, pos: BlockPos, explosion: Explosion) = super.onBlockExploded(state, level, pos, explosion)

    override fun validStateForTraversal(state: BlockState) = state.block === this && state.getValue(OPEN)

    override fun postTraverseEffect(level: Level, pos: BlockPos, state: BlockState, rift: Rift) {
        closeDoorBehind(level, pos)
        closeDoorBehind(level, pos.above())
    }

    protected fun closeDoorBehind(world: Level, pos: BlockPos) {
        world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue<Boolean?, Boolean?>(OPEN, false))
    }

    public override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val state = state.cycle(OPEN)
        world.setBlock(pos, state, 10)
        if (!world.isClientSide && state.getValue(WATERLOGGED)) world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))
        this.playSound(player, world, pos, state.getValue(OPEN))
        world.gameEvent(player, if (this.isOpen(state)) GameEvent.BLOCK_OPEN else GameEvent.BLOCK_CLOSE, pos)
        return InteractionResult.SUCCESS
    }

    public override fun canBeReplaced(blockState: BlockState, blockPlaceContext: BlockPlaceContext) = super.canBeReplaced(blockState, blockPlaceContext) || blockState.block === ModBlocks.DETACHED_RIFT.value()

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = if (state.getValue(HALF) == DoubleBlockHalf.UPPER) null else riftBlockEnityType.create(pos, state)

    public override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack?> {
        return getVisualBlockState(state).getDrops(params)
    }

    override fun getRiftPos(world: Level, pos: BlockPos, state: BlockState): BlockPos = if (state.getValue(HALF) == DoubleBlockHalf.LOWER) pos else pos.below()

    override fun providerType() = "Dimensional door"

    public override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.getValue(WATERLOGGED)) world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))

        val doubleBlockHalf = state.getValue(HALF)
        if (direction.axis === Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            if (neighborState.block is DoorBlock && neighborState.getValue(HALF) != doubleBlockHalf) {
                var copied = neighborState.setValue(HALF, doubleBlockHalf)
                if (copied.hasProperty(WATERLOGGED) && state.hasProperty(WATERLOGGED)) {
                    copied = copied.setValue(WATERLOGGED, state.getValue(WATERLOGGED))
                }
                return copied
            }
            return Blocks.AIR.defaultBlockState()
        } else {
            return if (doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos)) ModBlocks.DETACHED_RIFT.value().defaultBlockState() else state
        }
    }

    public override fun getInteractionShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos): VoxelShape = Shapes.block()

    override fun <R : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<R>
    ): BlockEntityTicker<R>? {
        return checkType(blockEntityType, riftBlockEnityType) { level, pos, state, rift -> RiftProvider.tickRift(level, pos, state, rift) }
    }

    override fun transformation(state: BlockState, pos: BlockPos): Matrix4dc {
        val facing = state.horizontalFacing!!
        return Matrix4d().inverseTranslateLocal(pos.center + (facing.normal * -0.31)).inverseRotateLocal(facing.opposite.eulerAngle)
    }

    override fun rotator(state: BlockState, pos: BlockPos): Matrix4dc = Matrix4d().inverseRotateLocal(state.horizontalFacing!!.opposite.eulerAngle)


    override fun isExitFlipped(): Boolean = true

    override fun isTall(cachedState: BlockState): Boolean = true

    override fun stateContainsRift(oldState: BlockState): Boolean = oldState.getValue(HALF) == DoubleBlockHalf.LOWER

    fun baseBlock() = BuiltInRegistries.BLOCK.get(getDimensionalDoorBlockRegistrar().get(BuiltInRegistries.BLOCK.getKey(this)))

    //    @Override
    //    protected @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
    //        return RenderShape.ENTITYBLOCK_ANIMATED;
    //    }

    override fun convertToRiftProvider(world: ServerLevel, pos: BlockPos, state: BlockState) = getRift(world, pos, state)

    override fun revertToBaseVariant(world: ServerLevel, pos: BlockPos, state: BlockState) {
        var state = getVisualBlockState(state)

        val upperPos: BlockPos?
        val upperState: BlockState?
        val lowerPos: BlockPos?
        val lowerState: BlockState?

        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            upperPos = pos
            upperState = state
            lowerPos = pos.below()
            lowerState = state.setValue(HALF, DoubleBlockHalf.LOWER)
        } else {
            upperPos = pos.above()
            upperState = state.setValue(HALF, DoubleBlockHalf.UPPER)
            lowerPos = pos
            lowerState = state
        }

        world.setBlock(lowerPos, lowerState, UPDATE_CLIENTS or UPDATE_KNOWN_SHAPE)
        world.setBlock(upperPos, upperState, UPDATE_CLIENTS or UPDATE_KNOWN_SHAPE)
    }

    override fun getPortalPlane(state: BlockState, pos: BlockPos): RiftUtils.PortalPlane = ofDoor(state, pos)

    override fun closeRift(level: Level, pos: BlockPos, state: BlockState) {
        val base = baseBlock()

        if (base is DoorBlock) {
            val newState = base.defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(OPEN, state.getValue(OPEN))
                .setValue(HINGE, state.getValue(HINGE))
                .setValue(POWERED, state.getValue(POWERED))
                .setValue(HALF, DoubleBlockHalf.LOWER)

            level.removeBlock(pos, false)
            level.setBlockAndUpdate(pos, newState)
            level.setBlockAndUpdate(pos.above(), newState.setValue(HALF, DoubleBlockHalf.UPPER))
        }
    }
}