package org.dimdev.dimdoors.world.feature;

import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class CustomOreFeatureConfig extends OreFeatureConfig {

    Predicate<BlockState> blockPredicate;

    public CustomOreFeatureConfig(Predicate<BlockState> blockPredicate, BlockState blockState, int size) {
        super(null, blockState, size);
        this.blockPredicate = blockPredicate;
    }


}