package org.dimdev.dimdoors.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.castOrNull
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

    override val riftBlockEnityType get() = ModBlockEntityTypes.GENERIC_RIFT.value()

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
        if (world.isClientSide()) return

        val strength = world.getBestNeighborSignal(pos)

        val rift = getRift(world, pos, state) ?: return

        attemptRedstoneTransmission(strength, rift)
    }

    companion object {
        val CODEC = simpleCodec(::LiminalTransmitterBlock)

        fun attemptRedstoneTransmission(strength: Int, rift: Rift): Boolean {
            rift.isStateDirty = false

            // Attempt a teleport
            try {
                val target = rift.target
                val location = target.castOrNull<LocationProvider>()?.location

                return target.`as`(Targets.REDSTONE)?.recieveSignal(strength, location) ?: false
            } catch (_: Exception) {
                return false
            }
        }
    }
}
