package org.dimdev.dimdoors.rift.registry

import org.dimdev.dimdoors.api.util.Location
import java.util.*

object RiftPlaceholder : RegistryVertex() {
    override fun getLocation(id: UUID): Location? = RiftRegistry.instance.locationOf(id)
}
