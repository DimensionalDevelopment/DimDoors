package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.MapCodec
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.util.CodecUtils.mutableList
import org.dimdev.dimdoors.util.LevelSpaceHelper
import java.util.*

class RiftRegistry : SubSystem<RiftRegistry>, VertexProvider {
    @JvmField
    var locationMap: MutableMap<Location?, Rift> = HashMap<Location?, Rift>()

    constructor()

    private constructor(vertices: MutableCollection<RegistryVertex>) {
        for (vertex in vertices) {
            require(vertex is Rift) { "RiftRegistry cannot load non-rift vertex $vertex" }
            requireNotNull(vertex.location) { "RiftRegistry cannot load rift without location " + vertex.id }
            this.locationMap[vertex.location] = vertex
        }
    }

    override fun collectVertices(): MutableList<out RegistryVertex> = this.locationMap.values.toMutableList()

    override fun type(): Type<RiftRegistry> = SubsystemTypes.RIFT


    private fun verticesForCodec(): MutableList<RegistryVertex> = this.locationMap.values.toMutableList()

    fun isRiftAt(location: Location?): Boolean {
        val possibleRift = this.locationMap[location]
        return possibleRift != null && possibleRift !is RiftPlaceholder
    }

    fun getRift(location: Location?): Rift {
        val rift = this.locationMap[location]
        requireNotNull(rift) { "There is no rift registered at $location" }
        return rift
    }

    fun getRift(id: UUID?): Rift {
        val rift = this.locationMap.values.firstOrNull { candidate -> candidate.id == id }
        require(!(rift == null || rift is RiftPlaceholder)) { "There is no rift registered with id $id" }
        return rift
    }

    fun findRift(id: UUID): Rift? {
        return this.locationMap.values
            .filter { rift -> rift.id == id }.firstOrNull { rift -> rift !is RiftPlaceholder }
    }

    fun getRiftOrPlaceholder(location: Location): Rift {
        var rift = this.locationMap[location]
        if (rift == null) {
            LOGGER.debug("Creating a rift placeholder at {}", location)
            rift = RiftPlaceholder(location)
            this.locationMap[location] = rift
            RiftGraph.getInstance().addVertex(rift)

            setDirty()
        }
        return rift
    }

    fun moveRift(oldLocation: Location, newLocation: Location) {
        this.moveRifts(mapOf(oldLocation to newLocation))
    }

    fun moveRifts(movements: Map<Location, Location>) {
        val filteredMovements = movements.filterNot { it.key == it }.takeUnless { it.isEmpty() } ?: return

        LOGGER.debug("Moving rifts {}", filteredMovements)

        val oldLocations = filteredMovements.keys
        val newLocations = mutableSetOf<Location>()

        for (newLocation in filteredMovements.values) {
            require(newLocations.add(newLocation)) { "Multiple rifts are moving to $newLocation" }
            require(!(this.locationMap.containsKey(newLocation) && !oldLocations.contains(newLocation))) { "There is already a rift registered at $newLocation" }
        }

        val riftGraph = RiftGraph.getInstance()

        oldLocations.associateWith { locationMap.remove(it)!! }.forEach { (location, rift) ->

            val newLocation = filteredMovements[location]!!

            this.locationMap[newLocation] = rift
            rift.world = newLocation.worldId
            rift.location = newLocation

            riftGraph.sources(rift).mapNotNull(this::findRift).forEach { it.targetMoved(rift) }
            riftGraph.targets(rift).mapNotNull(this::findRift).forEach { it.sourceMoved(rift) }

            rift.markDirty()
        }

        setDirty()
    }

    fun addRift(location: Location) {
        LOGGER.debug("Adding rift at {}", location)
        val rift = when (val currentRift = this.locationMap[location]) {
            is RiftPlaceholder -> {
                LOGGER.info("Converting a rift placeholder at $location into a rift")
                Rift(location).also { it.id = currentRift.id }
            }

            null -> Rift(location)
            else -> throw IllegalArgumentException("There is already a rift registered at $location")
        }

        RiftGraph.getInstance().addVertex(rift)
        this.locationMap[location] = rift
        rift.markDirty()
        LevelSpaceHelper.INSTANCE.onRiftAdded(rift)

        setDirty()
    }

    fun removeRift(location: Location?) {
        LOGGER.debug("Removing rift at {}", location)

        val rift = this.getRift(location)

        val riftGraph = RiftGraph.getInstance()

        riftGraph.sources(rift).mapNotNull(this::findRift).forEach { it.targetGone(rift) }
        riftGraph.targets(rift).mapNotNull(this::findRift).forEach { it.sourceGone(rift) }

        riftGraph.removeVertex(rift)
        this.locationMap.remove(location)

        setDirty()
    }


    private fun addEdge(from: RegistryVertex, to: RegistryVertex) {
        RiftGraph.getInstance().addEdge(from, to)

        if (from is Rift) {
            from.markDirty()
        }
        if (to is Rift) {
            to.markDirty()
        }
    }

    private fun removeEdge(from: RegistryVertex, to: RegistryVertex) {
        RiftGraph.getInstance().removeEdge(from, to)
        setDirty()
    }

    fun addLink(locationFrom: Location, locationTo: Location) {
        LOGGER.debug("Adding link {} -> {}", locationFrom, locationTo)

        val from = this.getRiftOrPlaceholder(locationFrom)
        val to = this.getRiftOrPlaceholder(locationTo)

        this.addEdge(from, to)

        // Notify the linked vertices of the change
        if (from !is RiftPlaceholder && to !is RiftPlaceholder) {
            from.targetAdded(to)
            to.sourceAdded(from)
        }

        setDirty()
    }

    fun removeLink(locationFrom: Location?, locationTo: Location?) {
        LOGGER.debug("Removing link {} -> {}", locationFrom, locationTo)

        val from = this.getRift(locationFrom)
        val to = this.getRift(locationTo)

        this.removeEdge(from, to)

        // Notify the linked vertices of the change
        from.targetGone(to)
        to.sourceGone(from)
        setDirty()
    }

    fun setProperties(location: Location?, properties: LinkProperties?) {
        LOGGER.debug("Setting DungeonLinkProperties for rift at {} to {}", location, properties)
        val rift = this.getRift(location)
        rift.properties = properties
        rift.markDirty()
        setDirty()
    }

    val rifts: MutableCollection<Rift> get() = this.locationMap.values

    fun getTargets(location: Location) = RiftGraph.getInstance()
        .targets(this.getRift(location))
        .mapNotNull { id: UUID -> this.findRift(id) }
        .map { obj -> obj.location }
        .toSet()

    fun getSources(location: Location): Set<Location> = RiftGraph.getInstance()
        .sources(this.getRift(location))
        .map(this::findRift)
        .filterNotNull()
        .map(Rift::location)
        .toSet()

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()
        private const val DATA_NAME = "rifts"

        val CODEC: MapCodec<RiftRegistry> = RegistryVertex.CODEC.mutableList().fieldOf(DATA_NAME)
            .xmap(::RiftRegistry, RiftRegistry::verticesForCodec)

        val instance: RiftRegistry
            get() = getInstance(SubsystemTypes.RIFT)!!

    }
}
