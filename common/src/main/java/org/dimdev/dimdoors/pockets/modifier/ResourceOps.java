package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.DelegatingOps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Optional;

public class ResourceOps<T> extends DelegatingOps<T> {
    private final ResourceManager resourceManager;
    private final FileToIdConverter converter;

    public ResourceOps(DynamicOps<T> delegate, ResourceManager resourceManager, FileToIdConverter converter) {
        super(delegate);
        this.resourceManager = resourceManager;
        this.converter = converter;
    }

    public ResourceLocation idToFile(ResourceLocation resourceLocation) {
        return this.converter.idToFile(resourceLocation);
    }

    public Optional<Resource> getResource(ResourceLocation resourceLocation) {
        return this.resourceManager.getResource(idToFile(resourceLocation));
    }
}