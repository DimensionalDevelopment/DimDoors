package org.dimdev.dimdoors.item;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class ModDataComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<RotatedLocation>> DESTINATION = register("destination", RotatedLocation.CODEC, RotatedLocation.STREAM_CODEC);
    public static final RegistrySupplier<DataComponentType<Integer>> COUNT = register("count", Codec.INT, ByteBufCodecs.VAR_INT.cast());
    public static final RegistrySupplier<DataComponentType<Set<UUID>>> KEY_IDS = register("key_ids", UUIDUtil.CODEC_LINKED_SET, ByteBufCodecs.collection(LinkedHashSet::new, UUIDUtil.STREAM_CODEC));
    public static final RegistrySupplier<DataComponentType<Byte>> MASK_WAND_MODE = register("mask_wand_mode", Codec.BYTE, ByteBufCodecs.BYTE.cast());
    public static final RegistrySupplier<DataComponentType<BlockPos>> MASK_WAND_PATROL_A = register("mask_wand_patrol_a", BlockPos.CODEC, BlockPos.STREAM_CODEC.cast());
    public static final RegistrySupplier<DataComponentType<BlockPos>> MASK_WAND_PATROL_B = register("mask_wand_patrol_b", BlockPos.CODEC, BlockPos.STREAM_CODEC.cast());


    private static <T> RegistrySupplier<DataComponentType<T>> register(String name, Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return DATA_COMPONENT_TYPES.register(name, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).cacheEncoding().build());
    }

    public static void register() {
        DATA_COMPONENT_TYPES.register();
    }
}
