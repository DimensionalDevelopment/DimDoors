package org.dimdev.dimcore;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public record FabricResourceLoader(ResourceLocation id, Consumer<ResourceManager> consumer,
                                   List<ResourceLocation> dependecies) implements IdentifiableResourceReloadListener, ResourceManagerReloadListener {

	@Override
	public ResourceLocation getFabricId() {
		return id;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return dependecies;
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		consumer.accept(resourceManager);
	}
}
