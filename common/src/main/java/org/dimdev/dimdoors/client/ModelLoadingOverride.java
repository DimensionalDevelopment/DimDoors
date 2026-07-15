package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public record ModelLoadingOverride(
        ModelResourceLocation replacementModel,
        List<Block> blockStateTargets,
        List<ModelResourceLocation> modelTargets
) {
    public static final String STANDALONE_VARIANT = "standalone";
    private static final String INVENTORY_MODEL_PREFIX = "item/";

    public ModelLoadingOverride {
        blockStateTargets = List.copyOf(blockStateTargets);
        modelTargets = List.copyOf(modelTargets);
    }

    public static ModelResourceLocation standalone(ResourceLocation modelId) {
        return new ModelResourceLocation(modelId, STANDALONE_VARIANT);
    }

    public static ModelLoadingOverride create(ModelResourceLocation replacementModel, Consumer<ModelLoadingRegistry> registration) {
        var builder = new Builder(replacementModel);
        registration.accept(builder);
        return builder.build();
    }

    public Stream<ModelResourceLocation> resolvedTargets() {
        return Stream.concat(
                blockStateTargets.stream()
                        .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                        .map(BlockModelShaper::stateToModelLocation),
                modelTargets.stream()
        );
    }

    public boolean targetsModel(ResourceLocation modelId) {
        return modelTargets.stream().anyMatch(target -> target.id().equals(modelId));
    }

    public Stream<ResourceLocation> resolvedTargetResources() {
        return modelTargets.stream().map(ModelLoadingOverride::modelResource);
    }

    public boolean targetsResource(ResourceLocation modelId) {
        return modelTargets.stream().anyMatch(target -> modelResource(target).equals(modelId));
    }

    private static ResourceLocation modelResource(ModelResourceLocation model) {
        if (ModelResourceLocation.INVENTORY_VARIANT.equals(model.variant())) {
            return model.id().withPrefix(INVENTORY_MODEL_PREFIX);
        }

        return model.id();
    }

    private static final class Builder implements ModelLoadingRegistry {
        private final ModelResourceLocation replacementModel;
        private final List<Block> blockStateTargets = new ArrayList<>();
        private final List<ModelResourceLocation> modelTargets = new ArrayList<>();

        private Builder(ModelResourceLocation replacementModel) {
            this.replacementModel = replacementModel;
        }

        @Override
        public void replaceBlockStates(Block block) {
            blockStateTargets.add(block);
        }

        @Override
        public void replaceModel(ModelResourceLocation model) {
            modelTargets.add(model);
        }

        private ModelLoadingOverride build() {
            return new ModelLoadingOverride(replacementModel, blockStateTargets, modelTargets);
        }
    }
}
