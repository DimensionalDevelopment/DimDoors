package org.dimdev.dimdoors.item;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.RotatedLocation;
import org.dimdev.dimdoors.entity.mask.MaskType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ModDataComponentTypes {

    public static final DataComponentType<RotatedLocation> DESTINATION = register("destination", RotatedLocation.CODEC, RotatedLocation.STREAM_CODEC);
    public static final DataComponentType<Integer> COUNT = register("count", Codec.INT, StreamCodec.of(RegistryFriendlyByteBuf::writeVarInt, RegistryFriendlyByteBuf::readVarInt));
    public static final DataComponentType<Set<UUID>> KEY_IDS = register("key_ids", UUIDUtil.CODEC_LINKED_SET, ByteBufCodecs.collection(LinkedHashSet::new, UUIDUtil.STREAM_CODEC));
    public static final DataComponentType<MaskType> MASK_TYPE = register("mask_type", MaskType.CODEC, MaskType.STREAM_CODEC);
    public static final DataComponentType<Integer> MASK_STACKS = register("mask_stacks", Codec.INT, StreamCodec.of(RegistryFriendlyByteBuf::writeVarInt, RegistryFriendlyByteBuf::readVarInt));
    public static final DataComponentType<List<BlockPos>> MASK_WAND_WAYPOINTS = register("mask_wand_waypoints", BlockPos.CODEC.listOf(), ByteBufCodecs.collection(ArrayList::new, BlockPos.STREAM_CODEC.cast()));
    public static final DataComponentType<MaskType> MASK_WAND_TYPE = register("mask_wand_type", MaskType.CODEC, MaskType.STREAM_CODEC);

    private static <T, V extends Codec<T>, U extends StreamCodec<RegistryFriendlyByteBuf, T>> DataComponentType<T> register(String name, V codec, U streamCodec) {
        return DimensionalDoors.getSided().registerDataComponentType(name, DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).cacheEncoding().build());
    }

    public static void register() {
    }
}
