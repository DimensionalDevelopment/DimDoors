package org.dimdev.dimdoors.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.block.entity.RiftBlockEntity
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.rift.targets.Targets

class LiminalTransmitterBlock(properties: Properties) : WaterLoggableBlockWithEntity(properties),
    RiftProvider<RiftBlockEntity.Impl> {
    override fun codec(): MapCodec<out BaseEntityBlock?> {
        return CODEC
    }

    override val riftBlockEnityType get() = ModBlockEntityTypes.GENERIC_RIFT

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        if (!ctx.level.getBlockState(ctx.clickedPos).`is`(ModBlocks.DETACHED_RIFT)) {
            return null
        }

        if (ctx.player == null || !ctx.player!!.isShiftKeyDown) return defaultBlockState()

        return super.getStateForPlacement(ctx)
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
        val rift = getRift(level, pos, state) ?: return
        attemptRedstoneTransmission(level.getBestNeighborSignal(pos), rift)
    }

    companion object {
        val CODEC: MapCodec<LiminalTransmitterBlock> = simpleCodec(::LiminalTransmitterBlock)

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
