package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.TraversableRiftBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GeneratedDoorModelMappings {
    public static final ResourceLocation PORTAL_ITEM_MODEL = DimensionalDoors.id("item/dimensional_portal");

    public record Mappings(
            List<Block> blocks,
            Map<ModelResourceLocation, ModelResourceLocation> blockModels,
            Map<ModelResourceLocation, ModelResourceLocation> itemModels
    ) {
    }

    public static Mappings create() {
        var blocks = DimensionalDoors.getDimensionalDoorBlockRegistrar()
                .getGennedIds()
                .stream()
                .map(BuiltInRegistries.BLOCK::get)
                .filter(TraversableRiftBlock.class::isInstance)
                .toList();

        var blockModels = new HashMap<ModelResourceLocation, ModelResourceLocation>();
        var itemModels = new HashMap<ModelResourceLocation, ModelResourceLocation>();

        for (var block : blocks) {
            var rift = (TraversableRiftBlock<?>) block;

            for (var state : block.getStateDefinition().getPossibleStates()) {
                blockModels.put(
                        BlockModelShaper.stateToModelLocation(state),
                        BlockModelShaper.stateToModelLocation(rift.getVisualBlockState(state))
                );
            }

            var original = rift.getVisualBlockState(block.defaultBlockState()).getBlock();

            itemModels.put(
                    ModelResourceLocation.inventory(
                            block.asItem().builtInRegistryHolder().key().location()
                    ),
                    ModelResourceLocation.inventory(
                            original.asItem().builtInRegistryHolder().key().location()
                    )
            );
        }

        return new Mappings(blocks, blockModels, itemModels);
    }

    private GeneratedDoorModelMappings() {
    }
}