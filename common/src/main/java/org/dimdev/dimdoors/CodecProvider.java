package org.dimdev.dimdoors;

import com.mojang.serialization.MapCodec;

public interface CodecProvider<T> {
    public MapCodec<? extends T> codec();
}
