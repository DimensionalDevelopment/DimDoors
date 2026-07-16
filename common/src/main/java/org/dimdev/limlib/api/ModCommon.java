package org.dimdev.limlib.api;

public interface ModCommon<T extends ISided<?>> {
    void init(T sided);
}
