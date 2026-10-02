package org.dimdev.dimdoors.client

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.resources.ResourceLocation

class GeneratedDoorModelCopyPlugin : ModelLoadingPlugin {
    override fun onInitializeModelLoader(context: ModelLoadingPlugin.Context) {
        val mappings = GeneratedDoorModelMappings.create()

        context.addModels(GeneratedDoorModelMappings.PORTAL_ITEM_MODELS)

        for (block in mappings.blocks) {
            context.registerBlockStateResolver(block) { ctx ->
                val placeholder = ctx!!.getOrLoadModel(
                    ResourceLocation.withDefaultNamespace("block/air")
                )
                for (state in block.getStateDefinition().getPossibleStates()) {
                    ctx.setModel(state, placeholder)
                }
            }
        }

        context.modifyModelAfterBake().register{ model, ctx ->
            val id = ctx!!.topLevelId() ?: return@register model

            val blockModel = mappings.blockModels[id]
            if (blockModel != null) return@register GeneratedDoorBakedModel(blockModel, null)

            val itemModel = mappings.itemModels[id]
            if (itemModel != null) return@register GeneratedDoorBakedModel(itemModel.source, itemModel.portal)
            model
        }
    }
}