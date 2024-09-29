package org.dimdev.dimdoors.world.neoforge;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.DataKeys;

import java.util.function.Supplier;

public class DataKeysImpl {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = net.neoforged.neoforge.registries.DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DimensionalDoors.MOD_ID);

    public static <T> DataKeys.DataKey<T> register(String name, Supplier<T> initializer, Codec<T> codec) {
        return new DataKeyImpl<>(ATTACHMENT_TYPES.register(name, id -> AttachmentType.builder(initializer).serialize(codec).build()));

    }

    public static record DataKeyImpl<T>(DeferredHolder<AttachmentType<?>, AttachmentType<T>> type) implements DataKeys.DataKey<T> {
        @Override
        public ResourceLocation getId() {
            return type.getId();
        }
    }
}
