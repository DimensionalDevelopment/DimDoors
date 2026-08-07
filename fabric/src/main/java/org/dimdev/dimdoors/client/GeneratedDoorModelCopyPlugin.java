package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

public final class GeneratedDoorModelCopyPlugin implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context context) {
        var mappings = GeneratedDoorModelMappings.create();

        context.addModels(GeneratedDoorModelMappings.PORTAL_ITEM_MODELS);

        for (var block : mappings.blocks()) {
            context.registerBlockStateResolver(block, ctx -> {
                var placeholder = ctx.getOrLoadModel(
                        ResourceLocation.withDefaultNamespace("block/air")
                );

                for (var state : block.getStateDefinition().getPossibleStates()) {
                    ctx.setModel(state, placeholder);
                }
            });
        }

        context.modifyModelAfterBake().register((model, ctx) -> {
            var id = ctx.topLevelId();
            if (id == null) {
                return model;
            }

            var blockModel = mappings.blockModels().get(id);
            if (blockModel != null) {
                return new GeneratedDoorBakedModel(blockModel, null);
            }

            var itemModel = mappings.itemModels().get(id);
            if (itemModel != null) {
                return new GeneratedDoorBakedModel(itemModel.source(), itemModel.portal());
            }

            return model;
        });
    }
}