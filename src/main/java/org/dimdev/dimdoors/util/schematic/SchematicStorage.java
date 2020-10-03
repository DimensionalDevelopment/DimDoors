package org.dimdev.dimdoors.util.schematic;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class SchematicStorage {
    private final ResourceManager resourceManager;
    private final Cache<Identifier, Schematic> cache = CacheBuilder.newBuilder().build();

    public SchematicStorage(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public Schematic get(Identifier id) {
        try {
            return cache.get(id, () -> {
                        try (InputStream in = resourceManager.getResource(id).getInputStream()) {
                            return Schematic.fromTag(NbtIo.readCompressed(in));
                        }
                    }
            );
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}