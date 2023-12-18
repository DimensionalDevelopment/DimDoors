package org.dimdev.dimdoors.saving;

public interface IPackable<T> {
    String name();

    T pack();

    boolean isModified();

    void clearModified();
}
