package org.dimdev.dimdoors.world;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.world.level.component.ChunkLazilyGeneratedComponent;

import java.util.function.Supplier;

public class DataKeys {
    public static final DataKey<ChunkLazilyGeneratedComponent> CHUNK_LAZILY_GENERATED = register("chunk_lazily_generated", () -> new ChunkLazilyGeneratedComponent(false), ChunkLazilyGeneratedComponent.CODEC);

    @ExpectPlatform
    public static <T> DataKey<T> register(String name, Supplier<T> initializer, Codec<T> codec) {
        throw new RuntimeException();
    }

    public static void init() {

    }

    public interface DataKey<T> {
        public ResourceLocation getId();
    }


}
