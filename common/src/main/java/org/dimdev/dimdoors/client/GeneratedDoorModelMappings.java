package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.TraversableRiftBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GeneratedDoorModelMappings {
    /**
     * Upright portal plane. Door items are flat {@code item/generated} sprites living in the same
     * plane, so the portal lines up with them as-is.
     */
    public static final ResourceLocation PORTAL_ITEM_MODEL = DimensionalDoors.id("item/dimensional_portal");

    /**
     * Horizontal portal slab matching {@link TrapDoorBlock#BOTTOM_AABB}, which is what the in-world
     * renderer draws for trapdoors. Trapdoor items use the 3d {@code block/*_trapdoor_bottom} model
     * rather than a sprite, so the upright plane would stand on edge through them.
     */
    public static final ResourceLocation PORTAL_FLAT_ITEM_MODEL = DimensionalDoors.id("item/dimensional_portal_flat");

    public static final List<ResourceLocation> PORTAL_ITEM_MODELS = List.of(PORTAL_ITEM_MODEL, PORTAL_FLAT_ITEM_MODEL);

    /**
     * @param source the vanilla item model the generated door item borrows
     * @param portal the portal model whose orientation matches {@code source}
     */
    public record ItemMapping(
            ModelResourceLocation source,
            ResourceLocation portal
    ) {
    }

    public record Mappings(
            List<Block> blocks,
            Map<ModelResourceLocation, ModelResourceLocation> blockModels,
            Map<ModelResourceLocation, ItemMapping> itemModels
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
        var itemModels = new HashMap<ModelResourceLocation, ItemMapping>();

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
                    new ItemMapping(
                            ModelResourceLocation.inventory(
                                    original.asItem().builtInRegistryHolder().key().location()
                            ),
                            portalModelFor(original)
                    )
            );
        }

        return new Mappings(blocks, blockModels, itemModels);
    }

    /**
     * Picks the portal model matching the shape of {@code visualBlock}'s item model.
     */
    public static ResourceLocation portalModelFor(Block visualBlock) {
        return visualBlock instanceof TrapDoorBlock ? PORTAL_FLAT_ITEM_MODEL : PORTAL_ITEM_MODEL;
    }

    private GeneratedDoorModelMappings() {
    }
}
