package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock;
import org.jetbrains.annotations.Nullable;

import static org.dimdev.dimdoors.block.UnravelUtil.copyState;
import static org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar.PREFIX;

public class DimensionalDoorsModelLoadingPlugin implements ModelLoadingPlugin {
    private static final ResourceLocation childItem = DimensionalDoors.id("item/child_item");
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        var resolver = new BlockStateResolver() {
            @Override
            public void resolveBlockStates(Context context) {
                var identifier = BuiltInRegistries.BLOCK.getKey(context.block());

                var blockRegistrar = DimensionalDoors.getDimensionalDoorBlockRegistrar();

                if (blockRegistrar.isMapped(identifier)) {
                    var mapped = blockRegistrar.get(identifier);

                    var original = BuiltInRegistries.BLOCK.get(mapped);

                    original.getStateDefinition().getPossibleStates().forEach(blockState -> {
                        var state = copyState(context.block(), blockState);
                        var model = context.getOrLoadModel(BlockModelShaper.stateToModelLocation(blockState));
                        context.setModel(state.setValue(WaterLoggableDoorBlock.WATERLOGGED, true), model);
                        context.setModel(state, model);
                    });
                }
            }
        };

        var modelResolver = new ModelResolver() {
            @Override
            public @Nullable UnbakedModel resolveModel(Context context) {
                if(context.id().getPath().contains(PREFIX)) {
                    return context.getOrLoadModel(childItem);
                }

                return null;
            }
        };

        pluginContext.resolveModel().register(modelResolver);

        DimensionalDoors.getDimensionalDoorBlockRegistrar().getGennedIds().stream().filter(BuiltInRegistries.BLOCK::containsKey).map(BuiltInRegistries.BLOCK::get).forEach(block -> pluginContext.registerBlockStateResolver(block, resolver));
        BuiltInRegistries.ITEM.registryKeySet().stream().map(ResourceKey::location).filter(a -> a.getPath().startsWith(PREFIX)).forEach(location -> pluginContext.addModels(new ModelResourceLocation(location, "inventory")));
    }

}
