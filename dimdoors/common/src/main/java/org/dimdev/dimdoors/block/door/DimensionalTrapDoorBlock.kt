package org.dimdev.dimdoors.block.door

import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
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
import org.dimdev.dimdoors.api.util.math.inverseRotateLocal
import org.dimdev.dimdoors.api.util.math.inverseTranslateLocal
import org.dimdev.dimdoors.block.DimensionalPortalBlock.Companion.checkType
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimdoors.block.TraversableRiftBlock
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.rift.RiftUtils.PortalPlane.Companion.ofTrapdoor
import org.joml.Matrix4d
import org.joml.Matrix4dc

abstract class DimensionalTrapDoorBlock<T : EntranceRiftBlockEntity<*>>(
    settings: Properties,
    blockSetType: BlockSetType
) : TrapDoorBlock(blockSetType, settings.pushReaction(PushReaction.BLOCK)), TraversableRiftBlock<T> {
    override fun entityInside(state: BlockState, world: Level, pos: BlockPos, entity: Entity) = super<TraversableRiftBlock>.entityInside(state, world, pos, entity)

    override fun onBlockExploded(state: BlockState, level: Level, pos: BlockPos, explosion: Explosion) = super.onBlockExploded(state, level, pos, explosion)

    override fun getPortalPlane(state: BlockState, pos: BlockPos) = ofTrapdoor(state, pos)

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

    public override fun canBeReplaced(blockState: BlockState, blockPlaceContext: BlockPlaceContext) = super.canBeReplaced(blockState, blockPlaceContext) || blockState.block === ModBlocks.DETACHED_RIFT.value()

    public override fun getDrops(state: BlockState, params: LootParams.Builder) = getEffectiveBlockState(state).getDrops(params)

    public override fun getInteractionShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos
    ): VoxelShape = Shapes.block()

    override fun transformation(state: BlockState, pos: BlockPos): Matrix4dc = Matrix4d().inverseTranslateLocal(pos.above().center)

    override fun rotator(state: BlockState, pos: BlockPos): Matrix4dc = Matrix4d().inverseRotateLocal(state.horizontalFacing!!.eulerAngle)

    fun getEffectiveBlockState(state: BlockState) = state

    override fun <R : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<R>
    ): BlockEntityTicker<R>? {
        return checkType(blockEntityType, riftBlockEnityType) { level, pos, state, rift -> RiftProvider.tickRift(level, pos, state, rift) }
    }

    fun baseBlock() = BuiltInRegistries.BLOCK.get(getDimensionalDoorBlockRegistrar().get(BuiltInRegistries.BLOCK.getKey(this)))

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
