package org.dimdev.dimdoors.rift.targets

import com.mojang.serialization.MapCodec
import net.minecraft.core.Rotations
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimcore.api.util.EntityUtils
import org.dimdev.dimdoors.api.rift.target.EntityTarget
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.pockets.PocketGenerator
import org.dimdev.dimdoors.world.pocket.PrivateRegistry
import org.dimdev.dimdoors.world.pocket.VirtualLocation
import org.dimdev.dimdoors.world.pocket.type.PocketColor
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket
import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddons
import java.util.*

//TODO: add the ability to do addon spefific EntityTarget stuff and use it seperate dyeable from PrivatePocket
object PrivatePocketTarget : VirtualTarget<PrivatePocketTarget>(), PlayerTrackingEntranceTarget<UUID, PrivatePocket, PrivateRegistry> {
    override fun processEntity(
        pocket: PrivatePocket,
        target: EntityTarget,
        entity: Entity,
        uuid: UUID,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3
    ): Boolean {
        if (entity is ItemEntity) {
            val stack = entity.item

            val dye = PocketColor.from(stack)

            if (dye == null) {
                return target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null)
            } else {
                val dyeableAddon =
                    pocket.getAddon<DyeableAddon>(PocketAddons.DYEABLE_ADDON)

                if (dyeableAddon == null) {
                    return target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null)
                } else {
                    val remaining = dyeableAddon.addDye(pocket, entity.owner, dye, stack.getCount())

                    if (remaining <= 0) {
                        entity.discard()
                    } else {
                        stack.count = remaining
                    }
                    return true
                }
            }
        } else {
            val received = target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null)
            if (received) {
                PrivateRegistry.instance.setExit(uuid, this.location)
            }
            return received
        }
    }

    override val type get() = VirtualTargets.PRIVATE

    override val color: RGBA = PrivatePocketExitTarget.color

    override fun copy(): PrivatePocketTarget = this

    override val subsystem: PrivateRegistry get() = PrivateRegistry.instance

    override val pocketClass = PrivatePocket::class.java

    override fun getKey(uuid: UUID?): UUID? = uuid

    override fun createPocket(virtualLocation: VirtualLocation): PrivatePocket? {
        val pocket = PocketGenerator.generatePrivatePocketV2(
            VirtualLocation(
                virtualLocation.world,
                virtualLocation.x,
                virtualLocation.z,
                -1
            )
        )

        return pocket.castOrNull<PrivatePocket>()
    }

    val codec = MapCodec.unit(PrivatePocketTarget)
}
