package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import org.dimdev.dimdoors.api.util.Edge
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.builder.GraphTypeBuilder
import java.util.*

class RiftGraph : SubSystem<RiftGraph> {
    private val graph: Graph<UUID, DefaultEdge> = GraphTypeBuilder
        .directed<UUID, DefaultEdge>()
        .allowingMultipleEdges(false)
        .allowingSelfLoops(true)
        .edgeClass(DefaultEdge::class.java)
        .buildGraph()

    constructor()

    constructor(edges: List<Edge>) {
        for (edge in edges) {
            this.graph.addVertex(edge.source)
            this.graph.addVertex(edge.target)
            this.graph.addEdge(edge.source, edge.target)
        }
    }

    override fun type(): Type<RiftGraph> {
        return SubsystemTypes.GRAPH.value()
    }

    fun clear() {
        if (this.graph.vertexSet().isEmpty()) return
        this.graph.removeAllVertices(this.graph.vertexSet().toList())
        this.setDirty()
    }

    fun rebuild(vertices: Collection<RegistryVertex>, edges: Collection<Edge>) {
        this.clear()
        this.addVertices(vertices)
        this.addEdges(edges)
    }

    fun refreshVertices(subsystems: Collection<SubSystem<*>>) {
        val vertices = LinkedHashSet<UUID>()
        for (subsystem in subsystems) {
            if (subsystem is VertexProvider) {
                subsystem.collectVertices().mapTo(vertices, RegistryVertex::id)
            }
        }

        vertices.forEach(this::addVertex)
        this.retainVertices(vertices)
    }

    fun addVertices(subsystem: SubSystem<*>) {
        if (subsystem is VertexProvider) {
            this.addVertices(subsystem.collectVertices())
        }
    }

    fun addVertices(vertices: Collection<RegistryVertex>) {
        vertices.forEach(this::addVertex)
    }

    fun addVertex(vertex: RegistryVertex): Boolean = this.addVertex(vertex.id)

    fun addVertex(vertex: UUID): Boolean = this.graph.addVertex(vertex).also { if (it) this.setDirty() }

    fun removeVertex(vertex: RegistryVertex): Boolean = this.removeVertex(vertex.id)

    fun removeVertex(vertex: UUID): Boolean = this.graph.removeVertex(vertex).also { if (it) this.setDirty() }

    fun containsVertex(vertex: RegistryVertex): Boolean = this.containsVertex(vertex.id)

    fun containsVertex(vertex: UUID): Boolean = this.graph.containsVertex(vertex)

    fun addEdge(source: RegistryVertex, target: RegistryVertex): Boolean = this.addEdge(source.id, target.id)

    fun addEdge(edge: Edge): Boolean = this.addEdge(edge.source, edge.target)

    fun addEdge(source: UUID, target: UUID): Boolean {
        if (!this.graph.containsVertex(source) || !this.graph.containsVertex(target)) return false

        return (this.graph.addEdge(source, target) != null).also { if (it) this.setDirty() }
    }

    fun addEdges(edges: Collection<Edge>) {
        edges.forEach(this::addEdge)
    }

    fun removeEdge(source: RegistryVertex, target: RegistryVertex): Boolean = this.removeEdge(source.id, target.id)

    fun removeEdge(edge: Edge): Boolean = this.removeEdge(edge.source, edge.target)

    fun removeEdge(source: UUID, target: UUID): Boolean = (this.graph.removeEdge(source, target) != null).also { if (it) this.setDirty() }

    fun containsEdge(source: RegistryVertex, target: RegistryVertex): Boolean = this.containsEdge(source.id, target.id)

    fun containsEdge(source: UUID, target: UUID): Boolean = this.graph.containsEdge(source, target)

    fun vertices(): Set<UUID> = this.graph.vertexSet()

    fun edges(): Set<Edge> = this.graph.edgeSet().mapTo(LinkedHashSet(), this::toEdge)

    fun targets(source: RegistryVertex): Set<UUID> = this.targets(source.id)

    fun targets(source: UUID): Set<UUID> {
        if (!this.graph.containsVertex(source)) return emptySet()
        return this.graph.outgoingEdgesOf(source).mapTo(LinkedHashSet(), this.graph::getEdgeTarget)
    }

    fun sources(target: RegistryVertex): Set<UUID> = this.sources(target.id)

    fun sources(target: UUID): Set<UUID> {
        if (!this.graph.containsVertex(target)) return emptySet()
        return this.graph.incomingEdgesOf(target).mapTo(LinkedHashSet(), this.graph::getEdgeSource)
    }

    fun outgoingEdges(source: UUID): Set<Edge> {
        if (!this.graph.containsVertex(source)) return emptySet()
        return this.graph.outgoingEdgesOf(source).mapTo(LinkedHashSet(), this::toEdge)
    }

    fun incomingEdges(target: UUID): Set<Edge> {
        if (!this.graph.containsVertex(target)) return emptySet()
        return this.graph.incomingEdgesOf(target).mapTo(LinkedHashSet(), this::toEdge)
    }

    fun followPointer(pointer: RegistryVertex): UUID? = this.followPointer(pointer.id)

    fun followPointer(pointer: UUID?): UUID? {
        if (pointer == null || !this.graph.containsVertex(pointer)) return null
        return this.graph.outgoingEdgesOf(pointer).firstOrNull()?.let(this.graph::getEdgeTarget)
    }

    fun retainVertices(vertices: Collection<UUID>) {
        val keep = vertices.toSet()
        val removed = this.graph.vertexSet().filterNot(keep::contains)
        if (removed.isEmpty()) return

        this.graph.removeAllVertices(removed)
        this.setDirty()
    }

    private fun toEdge(edge: DefaultEdge): Edge = Edge(this.graph.getEdgeSource(edge), this.graph.getEdgeTarget(edge))

    companion object {
        private val EDGE_CODEC: Codec<Edge> = RecordCodecBuilder.create { instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("source").forGetter(Edge::source),
                UUIDUtil.CODEC.fieldOf("target").forGetter(Edge::target)
            ).apply(instance, ::Edge)
        }

        val CODEC: MapCodec<RiftGraph> = EDGE_CODEC.listOf().fieldOf("edges").xmap(::RiftGraph) { it.edges().toList() }

        fun getInstance(): RiftGraph = getInstance(SubsystemTypes.GRAPH.value())!!
    }
}
