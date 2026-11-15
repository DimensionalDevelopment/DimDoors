package org.dimdev.dimdoors.rift.registry

import org.dimdev.dimdoors.api.util.Location
import java.util.*
import org.dimdev.dimdoors.rift.block.LocationRiftRegistry
import org.dimdev.dimdoors.rift.registry.RegistryVertex
import java.util.UUID

object RiftPlaceholder : RegistryVertex() {
    override fun getLocation(id: UUID): Location? = LocationRiftRegistry.instance.locationOf(id)
}