package org.dimdev.dimdoors;

public interface ModCommon<T extends ISided<?>> {
    void init(T sided);
}
