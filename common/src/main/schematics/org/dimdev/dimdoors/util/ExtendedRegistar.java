package org.dimdev.dimdoors.util;

import com.mojang.serialization.Codec;

public interface ExtendedRegistar<T> {
    public Codec<T> codec();
}
