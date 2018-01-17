package org.dimdev.dimdoors.ddutils;

import org.jgrapht.Graph;

public final class GraphUtils {
    public static <V, E> void replaceVertex(Graph<V, E> graph, V vertex, V replace) {
        graph.addVertex(replace);
        for (E edge : graph.outgoingEdgesOf(vertex)) graph.addEdge(replace, graph.getEdgeTarget(edge));
        for (E edge : graph.incomingEdgesOf(vertex)) graph.addEdge(graph.getEdgeSource(edge), replace);
        graph.removeVertex(vertex);
    }

    public static <V, E> V followPointer(Graph<V, E> graph, V pointer) {
        if (pointer != null) {
            E edge = graph.outgoingEdgesOf(pointer).stream().findFirst().orElse(null);
            return graph.getEdgeTarget(edge);
        }
        return null;
    }
}
