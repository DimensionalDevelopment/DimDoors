package org.dimdev.dimcore;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NeoforgeResourceLoader {
	public static class Server extends ContextAwareReloadListener implements ResourceManagerReloadListener {
		private final ResourceLocation id;
		private final BiConsumer<HolderLookup.Provider, ResourceManager> consumer;

		public Server(ResourceLocation id, BiConsumer<HolderLookup.Provider, ResourceManager> consumer) {
			this.id = id;
			this.consumer = consumer;
		}

		@Override
		public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
			consumer.accept(this.getRegistryLookup(), resourceManager);
		}

		@Override
		public String getName() {
			return id.toString();
		}
	}

	public static class Client implements ResourceManagerReloadListener {
		private final ResourceLocation id;
		private final Consumer<ResourceManager> consumer;

		public Client(ResourceLocation id, Consumer<ResourceManager> consumer) {
			this.id = id;
			this.consumer = consumer;
		}

		@Override
		public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
			consumer.accept(resourceManager);
		}

		@Override
		public String getName() {
			return id.toString();
		}
	}
}
