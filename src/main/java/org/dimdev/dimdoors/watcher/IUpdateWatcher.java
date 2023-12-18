package org.dimdev.dimdoors.watcher;

public interface IUpdateWatcher<T> {
    void onCreated(T message);

    void update(T message);

    void onDeleted(T message);
}
