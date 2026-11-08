package org.dimdev.dimdoors.block.door

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.dimdev.dimcore.api.VisualOutline
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.DimensionalDoors.Companion.getDimensionalDoorBlockRegistrar
import org.dimdev.dimdoors.api.util.IDimDoorsCollisionContext
import org.dimdev.dimdoors.api.util.horizontalFacing
import org.dimdev.dimdoors.api.util.math.eulerAngle
import org.dimdev.dimdoors.api.util.math.inverseRotateLocal
import org.dimdev.dimdoors.api.util.math.inverseTranslateLocal
import org.dimdev.dimdoors.block.DimensionalPortalBlock.Companion.checkType
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimdoors.block.TraversableRiftBlock
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.item.DetachRiftInteracting
import org.dimdev.dimdoors.rift.RiftUtils.PortalPlane.Companion.ofTrapdoor
import org.joml.Matrix4d
import org.joml.Matrix4dc

abstract class DimensionalTrapDoorBlock<T : EntranceRiftBlockEntity<T>>(
    settings: Properties,
    blockSetType: BlockSetType
) : TrapDoorBlock(blockSetType, settings.pushReaction(PushReaction.BLOCK)), TraversableRiftBlock<T>, VisualOutline {
    override fun entityInside(state: BlockState, world: Level, pos: BlockPos, entity: Entity) = super<TraversableRiftBlock>.entityInside(state, world, pos, entity)

    override fun onBlockExploded(state: BlockState, level: Level, pos: BlockPos, explosion: Explosion) = super<TraversableRiftBlock>.onBlockExploded(state, level, pos, explosion)

    override fun getPortalPlane(state: BlockState, pos: BlockPos) = ofTrapdoor(state, pos)

    public override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult =
        if (stack.item is DetachRiftInteracting) ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
        else super.useItemOn(stack, state, level, pos, player, hand, hitResult)

    public override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        var state = state
        state = state.cycle(OPEN)
        world.setBlock(pos, state, 10)
        if (!world.isClientSide && state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))
        }

        this.playSound(player, world, pos, state.getValue(OPEN))
        world.gameEvent(player, if (state.getValue(OPEN)) GameEvent.BLOCK_OPEN else GameEvent.BLOCK_CLOSE, pos)
        return InteractionResult.SUCCESS
    }

    public override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
        if(context.cast<IDimDoorsCollisionContext>().`dimensionalDoors$getHeldItem`().item is DetachRiftInteracting)
            Shapes.block()
        else
            super.getShape(state, level, pos, context)

    override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return if (!state.getValue(OPEN)) {
            if (state.getValue(HALF) == Half.TOP) TOP_AABB else BOTTOM_AABB
        } else {
            when (state.getValue(FACING)) {
                Direction.NORTH -> NORTH_OPEN_AABB
                Direction.SOUTH -> SOUTH_OPEN_AABB
                Direction.WEST -> WEST_OPEN_AABB
                Direction.EAST -> EAST_OPEN_AABB
                else -> NORTH_OPEN_AABB
            }
        }
    }


    public override fun canBeReplaced(blockState: BlockState, blockPlaceContext: BlockPlaceContext) = super.canBeReplaced(blockState, blockPlaceContext) || blockState.block === ModBlocks.DETACHED_RIFT

    public override fun getDrops(state: BlockState, params: LootParams.Builder): List<ItemStack> = getVisualBlockState(state).getDrops(params)

    public override fun getInteractionShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos
    ): VoxelShape = Shapes.block()

    override fun transformation(state: BlockState, pos: BlockPos): Matrix4dc = Matrix4d().inverseTranslateLocal(pos.above().center)

    override fun rotator(state: BlockState, pos: BlockPos): Matrix4dc = Matrix4d().inverseRotateLocal(state.horizontalFacing!!.eulerAngle)
    
    override fun <R : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<R>
    ): BlockEntityTicker<R>? {
        return checkType(blockEntityType, riftBlockEnityType) { level, pos, state, rift -> RiftProvider.tickRift(level, pos, state, rift) }
    }

    fun baseBlock() = BuiltInRegistries.BLOCK.get(getDimensionalDoorBlockRegistrar()[BuiltInRegistries.BLOCK.getKey(this)])

    override fun getRenderShape(blockState: BlockState) = RenderShape.MODEL

    override fun convertToRiftProvider(world: ServerLevel, pos: BlockPos, state: BlockState) = getRift(world, pos, state)

    override fun revertToBaseVariant(world: ServerLevel, pos: BlockPos, state: BlockState) {
        world.setBlock(pos, getVisualBlockState(state), UPDATE_CLIENTS or UPDATE_KNOWN_SHAPE)
    }

    override fun closeRift(level: Level, pos: BlockPos, state: BlockState) {
        val base = baseBlock()

        if (base is TrapDoorBlock) {
            val newState = base.defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(OPEN, state.getValue(OPEN))
                .setValue(POWERED, state.getValue(POWERED))
                .setValue(HALF, state.getValue(HALF))

            level.removeBlock(pos, false)
            level.setBlockAndUpdate(pos, newState)
        }
    }
}
