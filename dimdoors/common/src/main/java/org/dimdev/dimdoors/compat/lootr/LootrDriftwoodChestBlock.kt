package org.dimdev.dimdoors.compat.lootr

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.ChestType
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import noobanidus.mods.lootr.common.api.LootrAPI
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity

class LootrDriftwoodChestBlock(properties: Properties) : ChestBlock(properties, { BlockEntities.DRIFTWOOD_LOOTR_CHEST }) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = DriftwoodLootrChestBlockEntity(pos, state)


    override fun getExplosionResistance(): Float {
        return LootrAPI.getExplosionResistance(this, super.getExplosionResistance())
    }

    public override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        trace: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide() || player.isSpectator || (player !is ServerPlayer)) {
            return InteractionResult.CONSUME
        }
        if (player.isShiftKeyDown) {
            LootrAPI.handleProviderSneak(ILootrInfoProvider.of(pos, level), player)
        } else if (!isChestBlockedAt(level, pos)) {
            LootrAPI.handleProviderOpen(ILootrInfoProvider.of(pos, level), player)
        }
        return InteractionResult.SUCCESS
    }

    public override fun updateShape(
        stateIn: BlockState,
        facing: Direction,
        facingState: BlockState,
        worldIn: LevelAccessor,
        currentPos: BlockPos,
        facingPos: BlockPos
    ): BlockState {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn))
        }

        return stateIn
    }

    public override fun getShape(
        state: BlockState,
        worldIn: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return AABB
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val direction = context.horizontalDirection.opposite
        val fluidstate = context.level.getFluidState(context.clickedPos)
        return this.defaultBlockState().setValue(FACING, direction)
            .setValue(TYPE, ChestType.SINGLE)
            .setValue(WATERLOGGED, fluidstate.type === Fluids.WATER)
    }

    public override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    public override fun getMenuProvider(state: BlockState, worldIn: Level, pos: BlockPos): MenuProvider? {
        return null
    }

    public override fun hasAnalogOutputSignal(pState: BlockState): Boolean {
        return true
    }

    public override fun getDestroyProgress(
        pState: BlockState,
        pPlayer: Player,
        pLevel: BlockGetter,
        pPos: BlockPos
    ): Float {
        return LootrAPI.getDestroyProgress(
            pState,
            pPlayer,
            pLevel,
            pPos,
            super.getDestroyProgress(pState, pPlayer, pLevel, pPos)
        )
    }

    public override fun getAnalogOutputSignal(pBlockState: BlockState, pLevel: Level, pPos: BlockPos): Int {
        return LootrAPI.getAnalogOutputSignal(pBlockState, pLevel, pPos, 0)
    }

    override fun <T : BlockEntity> getTicker(
        pLevel: Level,
        pState: BlockState,
        pBlockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { level, pos, state, blockEntity -> ILootrBlockEntity.ticker(level, pos, state, blockEntity) }
    }

    public override fun tick(pState: BlockState, pLevel: ServerLevel, pPos: BlockPos, pRandom: RandomSource) {
        val blockentity = pLevel.getBlockEntity(pPos)
        if (blockentity is LootrChestBlockEntity) {
            blockentity.recheckOpen()
        }
    }

    override fun playerDestroy(
        level: Level,
        player: Player,
        blockPos: BlockPos,
        blockState: BlockState,
        blockEntity: BlockEntity?,
        itemStack: ItemStack
    ) {
        super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack)
        LootrAPI.playerDestroyed(level, player, blockPos, blockEntity)
    }
}