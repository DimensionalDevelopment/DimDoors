package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import net.minecraft.core.Rotations
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.TeleportUtil
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.VirtualLocation

object LimboTarget : VirtualTarget<LimboTarget>(), EntityTarget {
    val CODEC = MapCodec.unit(this)

    override fun receiveEntity(
        owner: Vertex,
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        var teleportPos = entity.blockPosition()
        while (ModDimensions.LIMBO_DIMENSION.getBlockState(
                VirtualLocation.getTopPos(
                    ModDimensions.LIMBO_DIMENSION,
                    teleportPos.x,
                    teleportPos.z
                )
            ).block === ModBlocks.ETERNAL_FLUID) { teleportPos = teleportPos.offset(1, 0, 1) }

        TeleportUtil.teleport(
            entity,
            ModDimensions.LIMBO_DIMENSION,
            teleportPos.atY(255),
            relativeAngle,
            relativeVelocity
        )
        return true
    }

    override val type get() = VirtualTargets.LIMBO

    override fun copy(): LimboTarget = this
}
