package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import net.minecraft.core.Rotations
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.targets.DungeonTarget.Companion.builder

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
        if (RANDOM.nextBoolean()) {
            return builder()
                .acceptedGroups(mutableSetOf(0))
                .coordFactor(1.0)
                .negativeDepthFactor(10000.0)
                .positiveDepthFactor(80.0)
                .weightMaximum(100.0)
                .noLink(false)
                .noLinkBack(false)
                .newRiftWeight(1f)
                .build()
                .`as`(Targets.ENTITY)!!
                .receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location)
        }

        return LimboTarget.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location)
    }

    val codec = MapCodec.unit(UnstableTarget)
    private val RANDOM = RandomSource.create()
}
