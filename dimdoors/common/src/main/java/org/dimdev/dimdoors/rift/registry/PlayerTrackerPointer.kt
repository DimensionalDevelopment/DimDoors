package org.dimdev.dimdoors.rift.registry

import net.minecraft.util.StringRepresentable
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.util.UUIDExtensions.derive
import java.util.*

abstract class PlayerTrackerPointer() : RegistryVertex() {
    abstract val subSystemType: SubSystem.Type<out PlayerTrackingSubSystem<*, *, *>>
    abstract val variant: Variant

    override fun getLocation(id: UUID): Location? {
        return SubSystem.getInstance(subSystemType)?.getRift(id)?.let(RiftRegistry.instance::locationOf)
    }

    override fun toString(): String = "PlayerRiftPointer(system=${this.subSystemType.name}, variant=$variant)"

    enum class Variant(val serialized: String) : StringRepresentable {
        Entrance("entrance"), Exit("exit");

        override fun getSerializedName(): String = serialized
        fun getTrackingId(playerId: UUID, type: SubSystem.Type<*>): UUID {
            return playerId.derive(serialized, type.name)
        }

        companion object {
//            val CODEC = StringRepresentable.fromEnum { entries.toTypedArray() }
        }
    }

    companion object {
        fun create(subSystemType: SubSystem.Type<out PlayerTrackingSubSystem<*, *, *>>, variant: Variant): PlayerTrackerPointer {
            return object : PlayerTrackerPointer() {
                override val subSystemType: SubSystem.Type<out PlayerTrackingSubSystem<*, *, *>> get() = subSystemType
                override val variant: Variant get() = variant
            }
        }
    }
}
