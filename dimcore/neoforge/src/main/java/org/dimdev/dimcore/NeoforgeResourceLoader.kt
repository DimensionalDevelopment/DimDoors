package org.dimdev.dimcore

import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.neoforged.neoforge.resource.ContextAwareReloadListener
import java.util.function.BiConsumer
import java.util.function.Consumer

class NeoforgeResourceLoader {
    class Server(
        private val id: ResourceLocation,
        private val consumer: (HolderLookup.Provider, ResourceManager) -> Unit
    ) : ContextAwareReloadListener(), ResourceManagerReloadListener {
        override fun onResourceManagerReload(resourceManager: ResourceManager) = consumer.invoke(registryLookup, resourceManager)
        override fun getName(): String = id.toString()
    }

    class Client(private val id: ResourceLocation, private val consumer: (ResourceManager) -> Unit) :
        ResourceManagerReloadListener {
        override fun onResourceManagerReload(resourceManager: ResourceManager) = consumer.invoke(resourceManager)
        override fun getName(): String = id.toString()
    }
}
