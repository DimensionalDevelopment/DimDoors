package org.dimdev.dimdoors.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimdoors.block.DimensionalPortalBlock.Companion.checkType
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity
import java.util.function.Function

class TesselatingLoomBlock(builder: Properties) : BaseEntityBlock(builder) {
    init {
        this.registerDefaultState(
            this.getStateDefinition().any().setValue<Direction?, Direction?>(FACING, Direction.NORTH)
        )
    }

    override fun codec(): MapCodec<out BaseEntityBlock?> {
        return CODEC
    }

    public override fun onRemove(
        oldState: BlockState,
        worldIn: Level,
        pos: BlockPos,
        newState: BlockState,
        isMoving: Boolean
    ) {
        if (!oldState.`is`(newState.getBlock())) {
            val tileEntity = worldIn.getBlockEntity(pos)
            if (tileEntity is TesselatingLoomBlockEntity) {
                val inventory = tileEntity.inventory
                Containers.dropContents(worldIn, pos, inventory)

                worldIn.updateNeighbourForOutputSignal(pos, this)
            }
        }
        super.onRemove(oldState, worldIn, pos, newState, isMoving)
    }


    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        entityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return createFurnaceTicker(level, entityType, ModBlockEntityTypes.TESSELATING_LOOM)
    }

    override fun newBlockEntity(bpos: BlockPos, bstate: BlockState): BlockEntity? {
        return TesselatingLoomBlockEntity(bpos, bstate)
    }

    protected fun openContainer(level: Level, bpos: BlockPos, player: ServerPlayer) {
        val be = level.getBlockEntity(bpos)
        if (be is TesselatingLoomBlockEntity) {
            player.openMenu(be)
            player.awardStat(Stats.INTERACT_WITH_FURNACE)
        } // end-if
        else {
            throw IllegalStateException("Our named container provider is missing!")
        }
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide()) {
            this.openContainer(level, blockPos, player as ServerPlayer)
        }

        return InteractionResult.SUCCESS
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection())
    }

    public override fun hasAnalogOutputSignal(state: BlockState): Boolean {
        return true
    }

    public override fun getAnalogOutputSignal(blockState: BlockState, level: Level, blockPos: BlockPos): Int {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos))
    }

    public override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue<Direction?, Direction?>(FACING, rotation.rotate(state.getValue<Direction?>(FACING)))
    }

    public override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.rotate(mirror.getRotation(state.getValue<Direction?>(FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(FACING)
    }

    public override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    companion object {
        val CODEC: MapCodec<TesselatingLoomBlock?> =
            simpleCodec<TesselatingLoomBlock?>(Function { builder: Properties? -> TesselatingLoomBlock(builder!!) })

        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
        private const val DISPLAY_NAME = ""

        protected fun <T : BlockEntity> createFurnaceTicker(
            level: Level,
            entityType: BlockEntityType<T>,
            entityTypeE: BlockEntityType<TesselatingLoomBlockEntity>
        ): BlockEntityTicker<T>? {
            return if (level.isClientSide()) null else checkType(entityType, entityTypeE) { _, _, _, blockEntity -> blockEntity.serverTick() }
        }
    }
}
