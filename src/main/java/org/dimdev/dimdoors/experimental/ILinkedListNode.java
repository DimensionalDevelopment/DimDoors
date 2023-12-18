package org.dimdev.dimdoors.experimental;

public interface ILinkedListNode<T> {
    ILinkedListNode<T> next();

    ILinkedListNode<T> prev();

    T data();

    void setData(T data);

    LinkedList<T> owner();

    T remove();
}
