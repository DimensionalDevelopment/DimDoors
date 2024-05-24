package org.dimdev.dimdoors.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class ResourceCodec implements Codec<Resource> {
    private final String startingPath;

    public ResourceCodec(String startingPath) {

        this.startingPath = startingPath;
    }
    private Codec<ResourceLocation> CODEC = ResourceLocation.CODEC.fieldOf("resource_key").codec();
    @Override
    public <T> DataResult<T> encode(Resource input, DynamicOps<T> ops, T prefix) {
        return DataResult.error(() -> "Encoding not supported");
    }

    @Override
    public <T> DataResult<Pair<Resource, T>> decode(DynamicOps<T> ops, T input) {
        if (ops instanceof ResourceOps<T> resourceOps) {
            return CODEC.parse(ops, input)
                .flatMap(
                    resourceLocation -> resourceOps.getResource(resourceLocation.withPath(startingPath))
                            .map(resource -> DataResult.success(Pair.of(resource, input)))
                        .orElseGet(
                            () -> DataResult.error(
                                () -> String.format("Resource %s not found", resourceOps.idToFile(resourceLocation))
                            )
                        )
                );
        } else {
            return DataResult.error(() -> "Not a resource ops");
        }
    }

    public static <T> Codec<T> inputStream(String startingPath, Function<InputStream, T> function) {
        return new ResourceCodec(startingPath).xmap(resource -> {
            try(var stream = resource.open()) {
                return function.apply(stream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, a -> null);
    }
}