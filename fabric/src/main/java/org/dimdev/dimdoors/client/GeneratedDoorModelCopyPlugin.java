package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

public final class GeneratedDoorModelCopyPlugin implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context context) {
        var mappings = GeneratedDoorModelMappings.create();

        context.addModels(GeneratedDoorModelMappings.PORTAL_ITEM_MODEL);

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

            var source = mappings.blockModels().get(id);
            if (source != null) {
                return new GeneratedDoorBakedModel(source, false);
            }

            source = mappings.itemModels().get(id);
            if (source != null) {
                return new GeneratedDoorBakedModel(source, true);
            }

            return model;
        });
    }
}