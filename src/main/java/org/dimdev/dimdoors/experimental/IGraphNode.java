package org.dimdev.dimdoors.experimental;

public interface IGraphNode<U, V> {
    Iterable<? extends IEdge<U, V>> inbound();

    Iterable<? extends IEdge<U, V>> outbound();

    int indegree();

    int outdegree();

    U data();
}
