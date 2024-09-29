package org.dimdev.dimdoors.world.fabric;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.DataKeys;

import java.util.function.Supplier;

public class DataKeysImpl {
    public static <T> DataKeys.DataKey<T> register(String name, Supplier<T> initializer, Codec<T> codec) {
        return new FabricDataKey<>(AttachmentRegistry.<T>builder().initializer(initializer).persistent(codec).buildAndRegister(DimensionalDoors.id(name)));
    }

    public static record FabricDataKey<T>(AttachmentType<T> type) implements DataKeys.DataKey {

        @Override
        public ResourceLocation getId() {
            return type.identifier();
        }
    }
}
