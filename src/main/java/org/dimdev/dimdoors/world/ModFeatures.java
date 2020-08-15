package org.dimdev.dimdoors.world;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.feature.CustomOreFeature;
import org.dimdev.dimdoors.world.feature.CustomOreFeatureConfig;

import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class ModFeatures {
    //public static final Feature<OreFeatureConfig> ORE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "custom_ore"), new CustomOreFeature(CustomOreFeatureConfig.CODEC));

    public static void init() {
        //CustomOreFeatureConfig eternalFluidFabricConfig = new CustomOreFeatureConfig(blockState -> blockState == ModBlocks.UNRAVELLED_FABRIC.getDefaultState(), ModBlocks.ETERNAL_FLUID.getDefaultState(), 16);
        //RangeDecoratorConfig eternalFluidFabricRange = new RangeDecoratorConfig(2, 0, 5, 24);
        //ModBiomes.LIMBO.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ORE.configure(eternalFluidFabricConfig).createDecoratedFeature(Decorator.COUNT_RANGE.configure(eternalFluidFabricRange)));

        //CustomOreFeatureConfig eternalFluidBedrockConfig = new CustomOreFeatureConfig(blockState -> blockState == Blocks.BEDROCK.getDefaultState(), ModBlocks.ETERNAL_FLUID.getDefaultState(), 16);
        //RangeDecoratorConfig eternalFluidBedrockRange = new RangeDecoratorConfig(1, 0, 0, 6);
        //ModBiomes.LIMBO.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ORE.configure(eternalFluidBedrockConfig).createDecoratedFeature(Decorator.COUNT_RANGE.configure(eternalFluidBedrockRange)));
    }
}
