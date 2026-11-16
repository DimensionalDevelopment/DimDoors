package org.dimdev.dimdoors.rift

import com.google.common.collect.HashBiMap
import com.google.common.collect.Maps
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.util.util.SimpleEvent
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.subsystem.graph.RegistryVertex
import org.dimdev.dimdoors.subsystem.graph.RegistryVertices
import org.dimdev.dimdoors.subsystem.graph.RiftGraph
import org.dimdev.dimdoors.subsystem.registry.SubSystem
import org.dimdev.dimdoors.util.ext.unboundedMap
import java.util.*

abstract class RiftRegistry<T: LocationProvider, R: RiftRegistry<T, R>>(
    rifts: Map<UUID, T> = emptyMap(),
    placeholders: Collection<UUID> = emptySet()
) : SubSystem<R>() {
    protected val bimap: HashBiMap<T, UUID> = HashBiMap.create<T, UUID>().apply { rifts.forEach { (id, location) -> put(location, id) } }
    protected val placeholders: MutableSet<UUID> = placeholders.toMutableSet()

    val rifts: Map<UUID, T> = Maps.filterKeys(bimap.inverse()) { it !in placeholders }
    fun isRiftAt(loc: T?) = bimap[loc]?.let { it !in placeholders } ?: false
    fun isPlaceholder(id: UUID): Boolean = id in placeholders
    fun idAt(loc: T) = bimap[loc]
    fun getRift(t: T): UUID = requireNotNull(bimap[t]) { "There is no rift registered at $t" }
    fun locationOf(id: UUID): T? = bimap.inverse()[id]
    fun getRiftOrPlaceholder(t: T): UUID = bimap[t] ?: UUID.randomUUID().also { id ->
        logger.debug("Creating a rift placeholder at {}", t)
        bimap[t] = id
        placeholders += id
        setDirty()
        events.placeholderAddEvent.invoker().placeholderAdded(id, t)
    }

    fun addRift(t: T): UUID {
        val id = when (val current = bimap[t]) {
            null -> UUID.randomUUID().also { bimap[t] = it }
            in placeholders -> current.also {
                logger.info("Converting a rift placeholder at {} into a rift", t)
                placeholders -= it
            }
            else -> throw IllegalArgumentException("There is already a rift registered at $t")
        }

        setDirty()
        events.riftAddedEvent.invoker().riftAdded(id, t)

        return id
    }

    fun removeRift(t: T): UUID {
        logger.debug("Removing rift at {}", t)

        val id = getRift(t)
        bimap.remove(t)
        placeholders -= id

        setDirty()
        events.riftRemoveEvent.invoker().riftRemoved(id, t)

        return id
    }

    fun moveRift(oldLocation: T, newLocation: T) = moveRifts(mapOf(oldLocation to newLocation))

    fun moveRifts(movements: Map<T, T>) {
        val filteredMovements = movements.filterNot { it.key == it.value }.takeUnless { it.isEmpty() } ?: return

        val oldLocations = filteredMovements.keys
        val newLocations = mutableSetOf<T>()

        for (newLocation in filteredMovements.values) {
            require(newLocations.add(newLocation)) { "Multiple rifts are moving to $newLocation" }
            require(newLocation !in bimap || newLocation in oldLocations) { "There is already a rift registered at $newLocation" }
        }

        val moved = oldLocations.associateWith { requireNotNull(bimap.remove(it)) { "There is no rift registered at $it" } }
        moved.forEach { (oldLocation, id) -> bimap[filteredMovements.getValue(oldLocation)] = id }

        setDirty()


        moved.forEach { (oldLocation, id) ->
            events.riftMoveEvent.invoker().riftMoved(id, oldLocation, filteredMovements.getValue(oldLocation))
        }
    }

    fun riftExists(riftId: UUID): Boolean = locationOf(riftId) != null && riftId !in placeholders


    abstract override fun type(): Type<R>

    abstract val events: RiftEvents<T>
    abstract val logger: Logger

    abstract class RiftEvents<E: Any> {
        fun interface PlaceholderAdded<T: Any> { fun placeholderAdded(id: UUID, t: T) }
        fun interface RiftAdded<T: Any> { fun riftAdded(id: UUID, t: T) }
        fun interface RiftRemoved<T: Any> { fun riftRemoved(id: UUID, t: T) }
        fun interface RiftMoved<T: Any> { fun riftMoved(id: UUID, from: T, to: T) }

        val placeholderAddEvent: SimpleEvent<PlaceholderAdded<E>> = SimpleEvent.of { callbacks -> { id, location -> callbacks.forEach { it.placeholderAdded(id, location) } } }
        val riftAddedEvent: SimpleEvent<RiftAdded<E>> = SimpleEvent.of { callbacks -> { id, location -> callbacks.forEach { it.riftAdded(id, location) } } }
        val riftRemoveEvent: SimpleEvent<RiftRemoved<E>> = SimpleEvent.of { callbacks -> { id, location -> callbacks.forEach { it.riftRemoved(id, location) } } }
        val riftMoveEvent: SimpleEvent<RiftMoved<E>> = SimpleEvent.of { callbacks -> { id, from, to -> callbacks.forEach { it.riftMoved(id, from, to) } } }

        abstract val vertexType: RegistryVertex

        open fun registerEvents(riftGraph: RiftGraph) {
            placeholderAddEvent.register { id, _ -> riftGraph.addVertex(id, RegistryVertices.RIFT_PLACEHOLDER) }
            riftAddedEvent.register { id, _ -> riftGraph.addVertex(id, vertexType) }
            riftRemoveEvent.register { id, _ -> riftGraph.removeVertex(id) }
            riftMoveEvent.register { id, _, _ -> riftGraph.moved(id) }
        }
    }

    companion object {
        fun <T : LocationProvider, R : RiftRegistry<T, R>> codec(
            codec: Codec<T>,
            name: String,
            function: (Map<UUID, T>, Collection<UUID>) -> R
        ): MapCodec<R> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                UUIDUtil.STRING_CODEC.unboundedMap(codec).fieldOf(name).forGetter { it.bimap.inverse().cast() },
                UUIDUtil.CODEC.listOf().optionalFieldOf("placeholders", emptyList())
                    .forGetter { it.placeholders.toList() }
            ).apply(instance, function)
        }
    }
}