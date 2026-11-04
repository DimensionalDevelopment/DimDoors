package org.dimdev.dimdoors.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.dimdev.dimcore.api.VisualOutline
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.DimensionalDoors.Companion.getDimensionalDoorBlockRegistrar
import org.dimdev.dimdoors.api.util.IDimDoorsCollisionContext
import org.dimdev.dimdoors.api.util.horizontalFacing
import org.dimdev.dimdoors.api.util.math.*
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.item.DetachRiftInteracting
import org.dimdev.dimdoors.rift.RiftUtils.PortalPlane.Companion.ofDoor
import org.joml.Matrix4d
import org.joml.Matrix4dc

class DimensionalPortalBlock(settings: Properties) : WaterLoggableBlockWithEntity(settings),
    TraversableRiftBlock<EntranceRiftBlockEntity.Impl>, VisualOutline {
    init {
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false))
    }

    override fun codec(): MapCodec<out BaseEntityBlock?> = CODEC

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(FACING)
    }

    public override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
        if(context.cast<IDimDoorsCollisionContext>().`dimensionalDoors$getHeldItem`().item is DetachRiftInteracting)
            Shapes.block()
        else
            Shapes.empty()

    override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return Shapes.empty()
    }

    override fun entityInside(state: BlockState, world: Level, pos: BlockPos, entity: Entity) = super<TraversableRiftBlock>.entityInside(state, world, pos, entity)

    override fun onBlockExploded(state: BlockState, level: Level, pos: BlockPos, explosion: Explosion) = super<TraversableRiftBlock>.onBlockExploded(state, level, pos, explosion)

    override fun postTraverseEffect(level: Level, pos: BlockPos, state: BlockState, rift: Rift<*>) = rift.detach()

    override fun getPortalPlane(state: BlockState, pos: BlockPos) = ofDoor(state, pos)

    public override fun canBeReplaced(blockState: BlockState, blockPlaceContext: BlockPlaceContext): Boolean = super.canBeReplaced(blockState, blockPlaceContext) || blockState.block === ModBlocks.DETACHED_RIFT

    override fun transformation(state: BlockState, pos: BlockPos): Matrix4dc {
        val facing = state.horizontalFacing ?: Direction.NORTH

        return Matrix4d().inverseTranslateLocal(pos.center + (facing.normal * -0.31)).rotateLocal(facing.opposite.eulerAngle)
    }

    override fun rotator(state: BlockState, pos: BlockPos): Matrix4dc {
        val facing = state.horizontalFacing ?: Direction.NORTH
        return Matrix4d().inverseRotateLocal(facing.eulerAngle)
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState = state.setValue(FACING, rotation.rotate(state.getValue(FACING)))

    override fun mirror(state: BlockState, mirror: Mirror): BlockState = if (mirror == Mirror.NONE) state else state.rotate(mirror.getRotation(state.getValue(FACING)))

    override fun isExitFlipped() = true

    override fun isTall(cachedState: BlockState) = true

    override val riftBlockEnityType: BlockEntityType<EntranceRiftBlockEntity.Impl> get() = ModBlockEntityTypes.ENTRANCE_RIFT

    override fun <T : BlockEntity> getTicker(
        world: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T?>? {
        return checkType(
            type,
            ModBlockEntityTypes.ENTRANCE_RIFT) { _: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: EntranceRiftBlockEntity.Impl ->
                blockEntity.tick(
                    world,
                    blockPos,
                    blockState
                )
            }
    }

    fun baseBlock(): Block {
        return BuiltInRegistries.BLOCK.get(getDimensionalDoorBlockRegistrar()[BuiltInRegistries.BLOCK.getKey(this)])
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.ENTITYBLOCK_ANIMATED
    }

    override fun closeRift(level: Level, pos: BlockPos, state: BlockState) {
        level.removeBlock(pos, false)
    }

    companion object {
        val CODEC: MapCodec<DimensionalPortalBlock> = simpleCodec(::DimensionalPortalBlock)

        @JvmField
        var FACING: DirectionProperty = HorizontalDirectionalBlock.FACING

        fun <E : BlockEntity, A : BlockEntity> checkType(
            givenType: BlockEntityType<A>,
            expectedType: BlockEntityType<E>,
            ticker: BlockEntityTicker<in E>
        ) = createTickerHelper(givenType, expectedType, ticker)
    }
}
