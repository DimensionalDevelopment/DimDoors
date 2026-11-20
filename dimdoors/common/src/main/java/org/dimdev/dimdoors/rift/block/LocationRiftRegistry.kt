package org.dimdev.dimdoors.rift.block

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.blockentity.BlockEntityRift
import org.dimdev.dimdoors.rift.RiftRegistry
import org.dimdev.dimdoors.subsystem.graph.RegistryVertex
import org.dimdev.dimdoors.subsystem.graph.RegistryVertices
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes
import org.dimdev.dimdoors.util.Location
import java.util.*

class LocationRiftRegistry(
    rifts: Map<UUID, Location> = emptyMap(),
    placeholders: Collection<UUID> = emptySet()) : RiftRegistry<Location, BlockEntityRift<*>, LocationRiftRegistry>(rifts, placeholders) {

    override fun type() = SubsystemTypes.LOCATION_RIFT
    override val events: RiftEvents<Location> get() = EVENTS
    override val logger: Logger get() = LOGGER

    override fun attach(stored: BlockEntityRift<*>, ref: Location): UUID = getRiftOrPlaceholder(ref)
    override fun detach(stored: BlockEntityRift<*>) {}
    override fun storedAt(ref: Location): BlockEntityRift<*>? = ref.blockEntity?.castOrNull<BlockEntityRift<*>>()

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
