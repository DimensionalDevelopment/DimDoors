package org.dimdev.limlib.impl.client;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.UnbakedModel;

import java.util.List;

public class DimensionalDoorsModelLoadingPlugin implements ModelLoadingPlugin {
    private final List<ModelLoadingOverride> overrides;

    public DimensionalDoorsModelLoadingPlugin(List<ModelLoadingOverride> overrides) {
        this.overrides = overrides;
    }

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.resolveModel().register(context -> {
            for (ModelLoadingOverride override : overrides) {
                if (override.targetsResource(context.id())) {
                    return context.getOrLoadModel(override.replacementModel().id());
                }
            }

            return null;
        });

        for (ModelLoadingOverride override : overrides) {
            BlockStateResolver resolver = new BlockStateResolver() {
                @Override
                public void resolveBlockStates(Context context) {
                    UnbakedModel model = context.getOrLoadModel(override.replacementModel().id());

                    for (var state : context.block().getStateDefinition().getPossibleStates()) {
                        context.setModel(state, model);
                    }
                }
            };

            override.blockStateTargets()
                    .forEach(block -> pluginContext.registerBlockStateResolver(block, resolver));
            override.resolvedTargetResources()
                    .forEach(pluginContext::addModels);
        }
    }
}
