package org.dimdev.dimdoors.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;

public interface MultiThreadedCodecs {
    static <K, V> UnboundedMapCodec<K, V> unboundedMap(Codec<K> keyCodec, Codec<V> elementCodec) {
        return new UnboundedMapCodec(keyCodec, elementCodec);
    }

}
