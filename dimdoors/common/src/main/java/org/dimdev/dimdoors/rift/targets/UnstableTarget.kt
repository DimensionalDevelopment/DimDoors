package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import net.minecraft.core.Rotations
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.rift.target.TargetResolver
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.RiftRegistry
import org.dimdev.dimdoors.world.ModDimensions

object UnstableTarget : VirtualTarget<UnstableTarget>(), EntityTarget {
    override val type get() = VirtualTargets.UNSTABLE

    override fun copy(): UnstableTarget {
        return this
    }

    override fun receiveEntity(
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        val candidates = RiftRegistry.instance.rifts
            .map { it.location }
            .filter { !ModDimensions.isPocketDimension(it.worldId) && it != locationOrNull }

        if (candidates.isNotEmpty()) {
            val destination = candidates[RANDOM.nextInt(candidates.size)]
            val target = TargetResolver.entity(destination)

            if (target != null) return target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, destination)
        }

        return LimboTarget.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location)
    }

    val codec = MapCodec.unit(UnstableTarget)
    private val RANDOM = RandomSource.create()
}
