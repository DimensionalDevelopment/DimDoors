package org.dimdev.dimdoors.client

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar

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

        context.resolveModel().register { ctx ->
            val id = ctx.id()
            if (id.namespace == "dimdoors" && id.path.startsWith("item/${DimensionalDoorItemRegistrar.PREFIX}")) {
                ctx.getOrLoadModel(ResourceLocation.withDefaultNamespace("block/air"))
            } else null
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