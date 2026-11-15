package org.dimdev.dimdoors.rift.block

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.rift.RiftRegistry
import org.dimdev.dimdoors.rift.registry.RegistryVertex
import org.dimdev.dimdoors.rift.registry.RegistryVertices
import org.dimdev.dimdoors.rift.registry.SubsystemTypes
import org.dimdev.dimdoors.api.util.Location
import java.util.*

class LocationRiftRegistry(
    rifts: Map<UUID, Location> = emptyMap(),
    placeholders: Collection<UUID> = emptySet()) : RiftRegistry<Location, LocationRiftRegistry>(rifts, placeholders) {

    override fun type() = SubsystemTypes.LOCATION_RIFT
    override val events: RiftEvents<Location> get() = EVENTS
    override val logger: Logger get() = LOGGER

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()

        val CODEC = codec(Location.CODEC, "locations", ::LocationRiftRegistry)

        val EVENTS = object : RiftEvents<Location>() {
            override val vertexType: RegistryVertex get() = RegistryVertices.LOCATION_RIFT
        }

        val instance: LocationRiftRegistry
            get() = getInstance(SubsystemTypes.LOCATION_RIFT)!!
    }
}
