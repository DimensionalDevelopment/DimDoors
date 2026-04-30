package org.dimdev.dimdoors.item;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.RotatedLocation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class ModDataComponentTypes {

    public static final DataComponentType<RotatedLocation> DESTINATION = register("destination", RotatedLocation.CODEC, RotatedLocation.STREAM_CODEC);
    public static final DataComponentType<Integer> COUNT = register("count", Codec.INT, StreamCodec.of(RegistryFriendlyByteBuf::writeVarInt, RegistryFriendlyByteBuf::readVarInt));
    public static final DataComponentType<Set<UUID>> KEY_IDS = register("key_ids", UUIDUtil.CODEC_LINKED_SET, ByteBufCodecs.collection(LinkedHashSet::new, UUIDUtil.STREAM_CODEC));

    private static <T, V extends Codec<T>, U extends StreamCodec<RegistryFriendlyByteBuf, T>> DataComponentType<T> register(String name, V codec, U streamCodec) {
        return DimensionalDoors.getSided().registerDataComponentType(name, DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).cacheEncoding().build());
    }

    public static void register() {
    }
}
