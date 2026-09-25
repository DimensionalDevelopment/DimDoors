package org.dimdev.dimcore

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import java.util.function.Consumer

@JvmRecord
data class FabricResourceLoader(val id: ResourceLocation, val consumer: (ResourceManager) -> Unit, val dependecies: MutableList<ResourceLocation>) : IdentifiableResourceReloadListener, ResourceManagerReloadListener {
    override fun getFabricId(): ResourceLocation = id
    override fun getFabricDependencies(): MutableCollection<ResourceLocation> = dependecies
    override fun onResourceManagerReload(resourceManager: ResourceManager) = consumer.invoke(resourceManager)
}
