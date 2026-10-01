package org.dimdev.dimdoors.rift.targets

import net.minecraft.core.Rotations
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.transfer.FluidUnit
import org.dimdev.dimcore.api.transfer.ItemUnit
import org.dimdev.dimcore.api.util.EntityUtils
import org.dimdev.dimdoors.api.rift.target.*
import org.dimdev.dimdoors.api.rift.target.TargetResolver.entity
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.TeleportUtil
import org.dimdev.dimdoors.util.LevelSpaceHelper

// A list of the default targets provided by dimcore. Add your own in ModTargets
object Targets {
    val ENTITY: Class<EntityTarget> = EntityTarget::class.java
    val ITEM: Class<ItemTarget> = ItemTarget::class.java
    val FLUID: Class<FluidTarget> = FluidTarget::class.java
    val REDSTONE: Class<RedstoneTarget> = RedstoneTarget::class.java

    fun registerDefaultTargets() {
        DefaultTargets.registerDefaultTarget(
            ENTITY,
            EntityTarget { entity: Entity, relativePos: Vec3, relativeRotation: Rotations, relativeVelocity: Vec3, location: Location? ->
                if (location != null) {
                    val targetLevel = location.world

                    entity(targetLevel, location.blockPos)?.let { return@EntityTarget it.receiveEntity(
                        entity,
                        relativePos,
                        relativeRotation,
                        relativeVelocity,
                        location
                    ) }

                    val localTargetPos = Vec3.upFromBottomCenterOf(location.blockPos, 0.0)
                    val frame = LevelSpaceHelper.INSTANCE.projectTeleportFrame(
                        targetLevel,
                        location,
                        localTargetPos,
                        relativeRotation,
                        relativeVelocity
                    )

                    TeleportUtil.teleport(entity, targetLevel, frame.pos, frame.angle, frame.velocity)
                    return@EntityTarget true
                }
                EntityUtils.chat(entity, Component.translatable("rifts.unlinked2"))
                false
            })

        DefaultTargets.registerDefaultTarget(ITEM, object : ItemTarget {
            override fun insert(unit: ItemUnit, simulate: Boolean): Long {
                return 0
            }

            override fun extract(unit: ItemUnit, simulate: Boolean): Long {
                return 0
            }

            override fun contents(): MutableList<ItemUnit> {
                return mutableListOf()
            }
        })

        DefaultTargets.registerDefaultTarget(FLUID, object : FluidTarget {
            override fun insert(unit: FluidUnit, simulate: Boolean): Long {
                return 0
            }

            override fun extract(unit: FluidUnit, simulate: Boolean): Long {
                return 0
            }

            override fun contents(): MutableList<FluidUnit> {
                return mutableListOf()
            }
        })

        DefaultTargets.registerDefaultTarget(REDSTONE, object : RedstoneTarget {
            override fun recieveSignal(strength: Int, location: Location?): Boolean {
                val targetLevel = location?.world ?: return false

                return TargetResolver.target<RedstoneTarget>(targetLevel, location.blockPos)?.recieveSignal(strength, location) ?: false
            }
        })
    }
}
