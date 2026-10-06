package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.math.MathUtil.entityEulerAngle
import org.dimdev.dimdoors.entity.limbo.LimboExitReason
import org.dimdev.dimdoors.fluid.ModFluids
import org.dimdev.dimdoors.rift.targets.EscapeTarget
import org.dimdev.dimdoors.util.UUIDExtensions.rift

class EternalFluidBlock(settings: Properties) : LiquidBlock(ModFluids.ETERNAL_FLUID, settings) {
    public override fun entityInside(blockState: BlockState, level: Level, blockPos: BlockPos, entity: Entity) {
        if (!level.isClientSide()) {
            try {
                if (TARGET.receiveEntity(Location.ofWorld(level as ServerLevel, blockPos).riftOrPlaceholder().rift(), entity, Vec3.ZERO, entityEulerAngle(entity), entity.deltaMovement, null)) {
                    if (entity is Player) LimboExitReason.ETERNAL_FLUID.broadcast(entity)
                }
            } catch (e: Throwable) {
                DimensionalDoors.LOGGER.error("Error when entering eternal fluid:", e)
            }
        }
    }

    companion object {
        private val TARGET: EntityTarget = EscapeTarget(true)
    }
}
