package org.dimdev.dimdoors.rift

import org.dimdev.dimdoors.util.Location
import java.util.*
import org.dimdev.dimdoors.rift.block.LocationRiftRegistry
import org.dimdev.dimdoors.subsystem.graph.RegistryVertex
import java.util.UUID

object RiftPlaceholder : RegistryVertex() {
    override fun getLocation(id: UUID): Location? = LocationRiftRegistry.instance.locationOf(id)
}