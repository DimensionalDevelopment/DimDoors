package org.dimdev.dimdoors.rift.block

import org.dimdev.dimdoors.blockentity.BlockEntityRift
import org.dimdev.dimdoors.rift.RiftRegistryVertex
import org.dimdev.dimdoors.subsystem.registry.SubSystem
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes
import org.dimdev.dimdoors.util.Location
import java.util.*

object LocationRift : RiftRegistryVertex<Location, BlockEntityRift<*>, LocationRiftRegistry, BlockEntityRift<*>>() {
    override val riftRegistry: SubSystem.Type<LocationRiftRegistry> get() = SubsystemTypes.LOCATION_RIFT

    override fun getRift(self: UUID): BlockEntityRift<*>? = getLocation(self)?.rift
}
