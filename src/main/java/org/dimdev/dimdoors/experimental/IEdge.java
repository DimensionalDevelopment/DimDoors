package org.dimdev.dimdoors.experimental;

public interface IEdge<U, V> {
    IGraphNode<U, V> head();

    IGraphNode<U, V> tail();

    V data();
}
