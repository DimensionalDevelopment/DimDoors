package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.Nullable;

import static org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar.PREFIX;

public class DimensionalDoorsModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        var resolver = new BlockStateResolver() {
            @Override
            public void resolveBlockStates(Context context) {
                if (DimensionalDoors.getDimensionalDoorBlockRegistrar().isMapped(BuiltInRegistries.BLOCK.getKey(context.block()))) {
                    var model = context.getOrLoadModel(DimensionalDoorsClient.childItem);

                    for (var state : context.block().getStateDefinition().getPossibleStates()) {
                        context.setModel(state, model);
                    }
                }
            }
        };

        var modelResolver = new ModelResolver() {
            @Override
            public @Nullable UnbakedModel resolveModel(Context context) {
                if(context.id().getPath().contains(PREFIX)) {
                    return context.getOrLoadModel(DimensionalDoorsClient.childItem);
                }

                return null;
            }
        };

        pluginContext.resolveModel().register(modelResolver);

        DimensionalDoors.getDimensionalDoorBlockRegistrar().getGennedIds().stream().filter(BuiltInRegistries.BLOCK::containsKey).map(BuiltInRegistries.BLOCK::get).forEach(block -> pluginContext.registerBlockStateResolver(block, resolver));
        BuiltInRegistries.ITEM.registryKeySet().stream().map(ResourceKey::location).filter(a -> a.getPath().startsWith(PREFIX)).forEach(location -> pluginContext.addModels(new ModelResourceLocation(location, "inventory").id()));
    }

}
