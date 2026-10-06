package org.dimdev.dimdoors.rift.registry

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import org.dimdev.dimdoors.api.util.Location
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.builder.GraphTypeBuilder
import java.util.*

class RiftGraph(saved: List<Vertex> = emptyList(), edges: List<Edge> = emptyList()) : SubSystem<RiftGraph>() {
    private val graph: Graph<UUID, DefaultEdge> = GraphTypeBuilder
        .directed<UUID, DefaultEdge>()
        .allowingMultipleEdges(false)
        .allowingSelfLoops(true)
        .edgeClass(DefaultEdge::class.java)
        .buildGraph()

    private val vertices = mutableMapOf<UUID, RegistryVertex>()

    init {
        saved.forEach {

            vertices[it.id] = it.type
            graph.addVertex(it.id)
        }
        edges.forEach { if (it.source in vertices && it.target in vertices) graph.addEdge(it.source, it.target) }
    }

    override fun type(): Type<RiftGraph> = SubsystemTypes.GRAPH

    fun vertex(id: UUID): RegistryVertex? = vertices[id]

    fun addVertex(id: UUID, vertex: RegistryVertex) {
        vertices[id] = vertex
        graph.addVertex(id)
        setDirty()
    }

    fun removeVertex(id: UUID, location: Location? = null): Boolean {
        val removed = Vertex(id, vertices[id] ?: return false)

        sources(id).forEach { vertices[it]?.targetGone(it, removed, location) }
        targets(id).forEach { vertices[it]?.sourceGone(it, removed, location) }

        vertices.remove(id)
        graph.removeVertex(id)
        setDirty()
        return true
    }

    fun addEdge(source: UUID, target: UUID): Boolean {
        val sourceType = vertices[source] ?: return false
        val targetType = vertices[target] ?: return false
        if (graph.addEdge(source, target) == null) return false

        setDirty()
        sourceType.targetAdded(source, Vertex(target, targetType))
        targetType.sourceAdded(target, Vertex(source, sourceType))
        return true
    }

    fun removeEdge(source: UUID, target: UUID): Boolean {
        if (graph.removeEdge(source, target) == null) return false

        setDirty()
        val sourceType = vertices[source] ?: return true
        val targetType = vertices[target] ?: return true
        sourceType.targetGone(source, Vertex(target, targetType), null)
        targetType.sourceGone(target, Vertex(source, sourceType), null)
        return true
    }

    fun changed(id: UUID) {
        val changed = Vertex(id, vertices[id] ?: return)
        sources(id).forEach { vertices[it]?.targetChanged(it, changed) }
    }

    fun targets(id: UUID): Set<UUID> {
        if (id !in vertices) return emptySet()
        return graph.outgoingEdgesOf(id).mapTo(linkedSetOf(), graph::getEdgeTarget)
    }

    fun sources(id: UUID): Set<UUID> {
        if (id !in vertices) return emptySet()
        return graph.incomingEdgesOf(id).mapTo(linkedSetOf(), graph::getEdgeSource)
    }

    private fun moved(id: UUID) {
        val moved = Vertex(id, vertices[id] ?: return)

        sources(id).forEach { vertices[it]?.targetMoved(it, moved) }
        targets(id).forEach { vertices[it]?.sourceMoved(it, moved) }
    }

    private fun edgeList(): List<Edge> = graph.edgeSet().map { Edge(graph.getEdgeSource(it), graph.getEdgeTarget(it)) }
    private fun vertexList(): List<Vertex> = vertices.map { Vertex(it.key, it.value) }

    companion object {
        val CODEC: MapCodec<RiftGraph> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Vertex.CODEC.listOf().fieldOf("vertices").forGetter(RiftGraph::vertexList),
                Edge.CODEC.listOf().fieldOf("edges").forGetter(RiftGraph::edgeList)
            ).apply(instance) { vertices, edges -> RiftGraph(vertices, edges) }
        }

        fun getInstance(): RiftGraph = getInstance(SubsystemTypes.GRAPH)!!

        fun registerEvents() {
            RiftRegistry.RiftEvents.PLACEHOLDER_ADDED.register { id, _ -> getInstance().addVertex(id, RegistryVertices.RIFT_PLACEHOLDER) }
            RiftRegistry.RiftEvents.RIFT_ADDED.register { id, _ ->
                getInstance().addVertex(id, RegistryVertices.RIFT)
                getInstance().changed(id)
            }
            RiftRegistry.RiftEvents.RIFT_REMOVED.register { id, location -> getInstance().removeVertex(id, location) }
            RiftRegistry.RiftEvents.RIFT_MOVED.register { id, _, _ -> getInstance().moved(id) }
            PocketRegistry.PocketEvents.ADDED_POCKET_ENTRANCE.register { id, location ->
                getInstance().addVertex(id, RegistryVertices.ENTRANCE)

                val rift = RiftRegistry.instance.getRift(location)
                getInstance().addEdge(id, rift)
            }
        }
    }

    data class Edge(val source: UUID, val target: UUID) {
        companion object {
            val CODEC: Codec<Edge> = RecordCodecBuilder.create { instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("source").forGetter(Edge::source),
                    UUIDUtil.CODEC.fieldOf("target").forGetter(Edge::target)
                ).apply(instance, ::Edge)
            }
        }
    }
}
