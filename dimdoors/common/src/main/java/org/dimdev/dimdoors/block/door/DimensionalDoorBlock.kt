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
import org.dimdev.dimcore.api.VisualOutline
import org.dimdev.dimdoors.DimensionalDoors.Companion.getDimensionalDoorBlockRegistrar
import org.dimdev.dimdoors.block.DimensionalPortalBlock.Companion.checkType
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimdoors.block.TraversableRiftBlock
import org.dimdev.dimdoors.item.IntrinsicRift
import org.dimdev.dimdoors.blockentity.BlockEntityRift
import org.dimdev.dimdoors.blockentity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.rift.RiftUtils
import org.dimdev.dimdoors.rift.RiftUtils.PortalPlane.Companion.ofDoor
import org.dimdev.dimdoors.util.Location
import org.dimdev.dimdoors.util.ext.eulerAngle
import org.dimdev.dimdoors.util.ext.horizontalFacing
import org.dimdev.dimdoors.util.ext.plus
import org.dimdev.dimdoors.util.ext.times
import org.dimdev.dimdoors.util.math.inverseRotateLocal
import org.dimdev.dimdoors.util.math.inverseTranslateLocal
import org.joml.Matrix4d
import org.joml.Matrix4dc

abstract class DimensionalDoorBlock<T : EntranceRiftBlockEntity<T>>(
    settings: Properties,
    blockSetType: BlockSetType,
    addWaterlog: Boolean
) : WaterLoggableDoorBlock(settings.pushReaction(PushReaction.BLOCK), blockSetType, addWaterlog), TraversableRiftBlock<T>, VisualOutline {
    override fun entityInside(state: BlockState, world: Level, pos: BlockPos, entity: Entity) = super<TraversableRiftBlock>.entityInside(state, world, pos, entity)

    override fun onBlockExploded(state: BlockState, level: Level, pos: BlockPos, explosion: Explosion) = super<TraversableRiftBlock>.onBlockExploded(state, level, pos, explosion)

    override fun validStateForTraversal(state: BlockState) = state.block === this && state.getValue(OPEN)

    override fun postTraverseEffect(level: Level, pos: BlockPos, state: BlockState, rift: BlockEntityRift<*>) {
        closeDoorBehind(level, pos)
        closeDoorBehind(level, pos.above())
    }

    protected open fun closeDoorBehind(world: Level, pos: BlockPos) {
        world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(OPEN, false))
    }

    override fun customDestroy(level: Level, pos: BlockPos, state: BlockState, i: Int, j: Int): Boolean? {
        if(state.getValue(HALF) == DoubleBlockHalf.UPPER) return null

        return super.customDestroy(level, pos, state, i, j)
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

    public override fun canBeReplaced(blockState: BlockState, blockPlaceContext: BlockPlaceContext) = super.canBeReplaced(blockState, blockPlaceContext) || blockState.block === ModBlocks.DETACHED_RIFT

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = if (state.getValue(HALF) == DoubleBlockHalf.UPPER) null else riftBlockEnityType.create(pos, state)

    public override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack?> {
        return getVisualBlockState(state).getDrops(params)
    }

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
            return if (doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos)) (if (world.getBlockEntity(pos) is IntrinsicRift) Blocks.AIR.defaultBlockState() else ModBlocks.DETACHED_RIFT.defaultBlockState()) else state
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

    fun baseBlock() = BuiltInRegistries.BLOCK.get(getDimensionalDoorBlockRegistrar()[BuiltInRegistries.BLOCK.getKey(this)])

    override fun convertToRiftProvider(world: ServerLevel, pos: BlockPos, state: BlockState) = getRift(world, pos, state)

    override fun revertToBaseVariant(location: Location) {
        val pos = location.blockPos
        val world = location.world
        val state = getVisualBlockState(world.getBlockState(pos))

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

    override fun getRiftPos(world: Level, pos: BlockPos, state: BlockState): BlockPos {
        return if(state.getValue(HALF) == DoubleBlockHalf.UPPER) pos.below() else pos
    }

    override fun closeRift(level: Level, pos: BlockPos, state: BlockState) {
        val base = baseBlock()

        if (base is DoorBlock) {
            val newState = base.defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(OPEN, state.getValue(OPEN))
                .setValue(HINGE, state.getValue(HINGE))
                .setValue(POWERED, state.getValue(POWERED))
                .setValue(HALF, DoubleBlockHalf.LOWER)

            level.setBlock(pos, newState, 18)
            level.setBlock(pos.above(), newState.setValue(HALF, DoubleBlockHalf.UPPER), 18)
        }
    }

    override fun isBase(): Boolean = false
}