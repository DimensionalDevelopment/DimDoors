package org.dimdev.dimdoors.rift.registry

import com.google.common.collect.HashBiMap
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.api.util.SimpleEvent
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.unboundedMap
import java.util.*

class RiftRegistry(
    rifts: Map<UUID, Location> = emptyMap(),
    placeholders: Collection<UUID> = emptySet()
) : SubSystem<RiftRegistry>() {
    private val locationMap: HashBiMap<Location, UUID> = HashBiMap.create<Location, UUID>().apply { rifts.forEach { (id, location) -> put(location, id) } }
    private val placeholders: MutableSet<UUID> = placeholders.toMutableSet()

    override fun type(): Type<RiftRegistry> = SubsystemTypes.RIFT

    val rifts: Map<UUID, Location> get() = locationMap.inverse().filterKeys { it !in placeholders }

    fun isRiftAt(location: Location): Boolean = locationMap[location]?.let { it !in placeholders } ?: false

    fun isPlaceholder(id: UUID): Boolean = id in placeholders

    fun idAt(location: Location): UUID? = locationMap[location]

    fun getRift(location: Location): UUID = requireNotNull(locationMap[location]) { "There is no rift registered at $location" }

    fun locationOf(id: UUID): Location? = locationMap.inverse()[id]

    fun getRiftOrPlaceholder(location: Location): UUID = locationMap[location] ?: UUID.randomUUID().also { id ->
        LOGGER.debug("Creating a rift placeholder at {}", location)
        locationMap[location] = id
        placeholders += id
        setDirty()
        RiftEvents.PLACEHOLDER_ADDED.invoker().placeholderAdded(id, location)
    }

    fun addRift(location: Location): UUID {
        val id = when (val current = locationMap[location]) {
            null -> UUID.randomUUID().also { locationMap[location] = it }
            in placeholders -> current.also {
                LOGGER.info("Converting a rift placeholder at {} into a rift", location)
                placeholders -= it
            }
            else -> throw IllegalArgumentException("There is already a rift registered at $location")
        }

        setDirty()
        RiftEvents.RIFT_ADDED.invoker().riftAdded(id, location)

        return id
    }

    fun removeRift(location: Location): UUID {
        LOGGER.debug("Removing rift at {}", location)

        val id = getRift(location)
        locationMap.remove(location)
        placeholders -= id

        setDirty()
        RiftEvents.RIFT_REMOVED.invoker().riftRemoved(id, location)

        return id
    }

    fun moveRift(oldLocation: Location, newLocation: Location) = moveRifts(mapOf(oldLocation to newLocation))

    fun moveRifts(movements: Map<Location, Location>) {
        val filteredMovements = movements.filterNot { it.key == it.value }.takeUnless { it.isEmpty() } ?: return

        val oldLocations = filteredMovements.keys
        val newLocations = mutableSetOf<Location>()

        for (newLocation in filteredMovements.values) {
            require(newLocations.add(newLocation)) { "Multiple rifts are moving to $newLocation" }
            require(newLocation !in locationMap || newLocation in oldLocations) { "There is already a rift registered at $newLocation" }
        }

        val moved = oldLocations.associateWith { requireNotNull(locationMap.remove(it)) { "There is no rift registered at $it" } }
        moved.forEach { (oldLocation, id) -> locationMap[filteredMovements.getValue(oldLocation)] = id }

        setDirty()


        moved.forEach { (oldLocation, id) ->
            RiftEvents.RIFT_MOVED.invoker().riftMoved(id, oldLocation, filteredMovements.getValue(oldLocation))
        }
    }

    fun riftExists(riftId: UUID): Boolean = locationOf(riftId) != null && riftId !in placeholders

    object RiftEvents {
        val PLACEHOLDER_ADDED: SimpleEvent<PlaceholderAdded> = SimpleEvent.of { callbacks -> { id, location -> callbacks.forEach { it.placeholderAdded(id, location) } } }
        val RIFT_ADDED: SimpleEvent<RiftAdded> = SimpleEvent.of { callbacks -> { id, location -> callbacks.forEach { it.riftAdded(id, location) } } }
        val RIFT_REMOVED: SimpleEvent<RiftRemoved> = SimpleEvent.of { callbacks -> { id, location -> callbacks.forEach { it.riftRemoved(id, location) } } }
        val RIFT_MOVED: SimpleEvent<RiftMoved> = SimpleEvent.of { callbacks -> { id, from, to -> callbacks.forEach { it.riftMoved(id, from, to) } } }

        fun interface PlaceholderAdded { fun placeholderAdded(id: UUID, location: Location) }
        fun interface RiftAdded { fun riftAdded(id: UUID, location: Location) }
        fun interface RiftRemoved { fun riftRemoved(id: UUID, location: Location) }
        fun interface RiftMoved { fun riftMoved(id: UUID, from: Location, to: Location) }
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()

        val CODEC: MapCodec<RiftRegistry> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                UUIDUtil.STRING_CODEC.unboundedMap(Location.CODEC).fieldOf("locations").forGetter { it.locationMap.inverse() },
                UUIDUtil.CODEC.listOf().optionalFieldOf("placeholders", emptyList()).forGetter { it.placeholders.toList() }
            ).apply(instance) { rifts, placeholders -> RiftRegistry(rifts, placeholders) }
        }

        val instance: RiftRegistry
            get() = getInstance(SubsystemTypes.RIFT)!!
    }
}
