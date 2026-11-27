package org.dimdev.dimcore

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import org.dimdev.dimcore.util.DatapackLoader

class DimCoreFabric : ModInitializer {
    override fun onInitialize() {

        val id = ResourceLocation.fromNamespaceAndPath(DimCore.MOD_ID, "datapack_loader")
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(id) { provider -> FabricResourceLoader(id, { DatapackLoader.reload(provider, it) }, mutableListOf()) }
    }
}
