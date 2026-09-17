package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import org.dimdev.dimdoors.api.util.Edge;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RiftGraph extends SubSystem<RiftGraph> {
    private static final Codec<Edge> EDGE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("source").forGetter(Edge::source),
            UUIDUtil.CODEC.fieldOf("target").forGetter(Edge::target)
    ).apply(instance, Edge::new));

    public static final MapCodec<RiftGraph> CODEC = EDGE_CODEC.listOf().fieldOf("edges").xmap(RiftGraph::new, RiftGraph::edgesForCodec);

    private final Graph<UUID, DefaultEdge> graph = GraphTypeBuilder
            .<UUID, DefaultEdge>directed()
            .allowingMultipleEdges(true)
            .allowingSelfLoops(true)
            .edgeClass(DefaultEdge.class)
            .buildGraph();

    public RiftGraph() {
    }

    public RiftGraph(Collection<Edge> edges) {
        for (Edge edge : edges) {
            this.graph.addVertex(edge.source());
            this.graph.addVertex(edge.target());
            this.graph.addEdge(edge.source(), edge.target());
        }
    }

    @Override
    public Type<RiftGraph> type() {
        return SubsystemTypes.GRAPH;
    }

    public static RiftGraph getInstance() {
        return getInstance(SubsystemTypes.GRAPH);
    }

    public void clear() {
        this.graph.removeAllVertices(Set.copyOf(this.graph.vertexSet()));
        this.setDirty();
    }

    public void rebuild(Collection<? extends RegistryVertex> vertices, Collection<Edge> edges) {
        this.clear();
        this.addVertices(vertices);

        for (Edge edge : edges) {
            if (this.containsVertex(edge.source()) && this.containsVertex(edge.target())) {
                this.addEdge(edge);
            }
        }
    }

    public void refreshVertices(Collection<? extends SubSystem<?>> subsystems) {
        Set<UUID> vertices = new LinkedHashSet<>();
        for (SubSystem<?> subsystem : subsystems) {
            if (subsystem instanceof VertexProvider provider) {
                provider.collectVertices().stream()
                        .map(RegistryVertex::getId)
                        .forEach(vertices::add);
            }
        }

        for (UUID vertex : vertices) {
            this.addVertex(vertex);
        }
        this.retainVertices(vertices);
    }

    public void addVertices(SubSystem<?> subsystem) {
        if (subsystem instanceof VertexProvider provider) {
            this.addVertices(provider.collectVertices());
        }
    }

    public void addVertices(Collection<? extends RegistryVertex> vertices) {
        for (RegistryVertex vertex : vertices) {
            this.addVertex(vertex);
        }
    }

    public boolean addVertex(RegistryVertex vertex) {
        return this.addVertex(vertex.getId());
    }

    public boolean addVertex(UUID vertex) {
        boolean added = this.graph.addVertex(vertex);
        if (added) {
            this.setDirty();
        }
        return added;
    }

    public boolean removeVertex(RegistryVertex vertex) {
        return this.removeVertex(vertex.getId());
    }

    public boolean removeVertex(UUID vertex) {
        boolean removed = this.graph.removeVertex(vertex);
        if (removed) {
            this.setDirty();
        }
        return removed;
    }

    public boolean containsVertex(RegistryVertex vertex) {
        return this.containsVertex(vertex.getId());
    }

    public boolean containsVertex(UUID vertex) {
        return this.graph.containsVertex(vertex);
    }

    public boolean addEdge(RegistryVertex source, RegistryVertex target) {
        return this.addEdge(source.getId(), target.getId());
    }

    public boolean addEdge(Edge edge) {
        return this.addEdge(edge.source(), edge.target());
    }

    public boolean addEdge(UUID source, UUID target) {
        if (!this.graph.containsVertex(source) || !this.graph.containsVertex(target)) {
            return false;
        }

        boolean added = this.graph.addEdge(source, target) != null;
        if (added) {
            this.setDirty();
        }
        return added;
    }

    public void addEdges(Collection<Edge> edges) {
        for (Edge edge : edges) {
            this.addEdge(edge);
        }
    }

    public boolean removeEdge(RegistryVertex source, RegistryVertex target) {
        return this.removeEdge(source.getId(), target.getId());
    }

    public boolean removeEdge(Edge edge) {
        return this.removeEdge(edge.source(), edge.target());
    }

    public boolean removeEdge(UUID source, UUID target) {
        boolean removed = this.graph.removeEdge(source, target) != null;
        if (removed) {
            this.setDirty();
        }
        return removed;
    }

    public boolean containsEdge(RegistryVertex source, RegistryVertex target) {
        return this.containsEdge(source.getId(), target.getId());
    }

    public boolean containsEdge(UUID source, UUID target) {
        return this.graph.containsEdge(source, target);
    }

    public Set<UUID> vertices() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(this.graph.vertexSet()));
    }

    public Set<Edge> edges() {
        return this.graph.edgeSet().stream()
                .map(this::toEdge)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<UUID> targets(RegistryVertex source) {
        return this.targets(source.getId());
    }

    public Set<UUID> targets(UUID source) {
        if (!this.graph.containsVertex(source)) {
            return Collections.emptySet();
        }

        return this.graph.outgoingEdgesOf(source).stream()
                .map(this.graph::getEdgeTarget)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<UUID> sources(RegistryVertex target) {
        return this.sources(target.getId());
    }

    public Set<UUID> sources(UUID target) {
        if (!this.graph.containsVertex(target)) {
            return Collections.emptySet();
        }

        return this.graph.incomingEdgesOf(target).stream()
                .map(this.graph::getEdgeSource)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Edge> outgoingEdges(UUID source) {
        if (!this.graph.containsVertex(source)) {
            return Collections.emptySet();
        }

        return this.graph.outgoingEdgesOf(source).stream()
                .map(this::toEdge)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Edge> incomingEdges(UUID target) {
        if (!this.graph.containsVertex(target)) {
            return Collections.emptySet();
        }

        return this.graph.incomingEdgesOf(target).stream()
                .map(this::toEdge)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public UUID followPointer(RegistryVertex pointer) {
        return this.followPointer(pointer.getId());
    }

    public UUID followPointer(UUID pointer) {
        if (pointer == null || !this.graph.containsVertex(pointer)) {
            return null;
        }

        return this.graph.outgoingEdgesOf(pointer).stream()
                .findFirst()
                .map(this.graph::getEdgeTarget)
                .orElse(null);
    }

    public void retainVertices(Collection<UUID> vertices) {
        Set<UUID> retainedVertices = new LinkedHashSet<>(vertices);
        for (UUID vertex : Set.copyOf(this.graph.vertexSet())) {
            if (!retainedVertices.contains(vertex)) {
                this.graph.removeVertex(vertex);
                this.setDirty();
            }
        }
    }

    private Edge toEdge(DefaultEdge edge) {
        return new Edge(this.graph.getEdgeSource(edge), this.graph.getEdgeTarget(edge));
    }

    private List<Edge> edgesForCodec() {
        return new ArrayList<>(this.edges());
    }
}
