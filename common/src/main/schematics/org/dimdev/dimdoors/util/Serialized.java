package org.dimdev.dimdoors.util;

import com.mojang.serialization.MapCodec;

public interface Serialized<T extends Serialized<T>> {
    public interface SerializedType<T extends Serialized<T>> {
        MapCodec<T> mapCodec();
    }

    public <V extends Serialized<T>> V getType();
}
