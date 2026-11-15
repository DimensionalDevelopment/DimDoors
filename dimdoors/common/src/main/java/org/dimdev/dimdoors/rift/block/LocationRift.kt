package org.dimdev.dimdoors.rift.block

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import org.dimdev.dimdoors.blockentity.BlockEntityRift
import org.dimdev.dimdoors.rift.RiftRegistryVertex
import org.dimdev.dimdoors.rift.registry.SubSystem
import org.dimdev.dimdoors.rift.registry.SubsystemTypes
import org.dimdev.dimdoors.api.util.Location
import java.util.*

object LocationRift : RiftRegistryVertex<Location, LocationRiftRegistry, BlockEntityRift<*>>() {
    override val riftRegistry: SubSystem.Type<LocationRiftRegistry> get() = SubsystemTypes.LOCATION_RIFT

    override fun getRift(self: UUID): BlockEntityRift<*>? = getLocation(self)?.rift


    private val LOGGER: Logger = LogManager.getLogger()
}
