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
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class ModDataComponentTypes {

    public static final DataComponentType<RotatedLocation> DESTINATION = register("destination", RotatedLocation.CODEC, RotatedLocation.STREAM_CODEC);
    public static final DataComponentType<Integer> COUNT = register("count", Codec.INT, StreamCodec.of(RegistryFriendlyByteBuf::writeVarInt, RegistryFriendlyByteBuf::readVarInt));
    public static final DataComponentType<VirtualTarget<?>> VIRTUAL_TARGET = register("virtual_target", VirtualTarget.CODEC);

    private static <T, V extends Codec<T>, U extends StreamCodec<RegistryFriendlyByteBuf, T>> DataComponentType<T> register(String name, V codec) {
        return register(name, codec, null);
    }

    private static <T, V extends Codec<T>, U extends StreamCodec<RegistryFriendlyByteBuf, T>> DataComponentType<T> register(String name, V codec, U streamCodec) {
        var builder = DataComponentType.<T>builder().persistent(codec);
        if(streamCodec != null) builder.networkSynchronized(streamCodec).cacheEncoding();
        return DimensionalDoors.getSided().registerDataComponentType(name, builder.build());
    }

    public static void register() {}
}
