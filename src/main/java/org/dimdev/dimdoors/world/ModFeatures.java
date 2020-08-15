package org.dimdev.dimdoors.world;

import org.dimdev.dimdoors.block.ModBlocks;

import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public final class ModFeatures {
    public static final ConfiguredFeature<?, ?> LIMBO_BEDROCK_FLUID_ORE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "limbo_bedrock_fluid_ore"), Feature.ORE.configure(new OreFeatureConfig(new BlockMatchRuleTest(Blocks.BEDROCK), ModBlocks.ETERNAL_FLUID.getDefaultState(), 6)).decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(0, 5, 7))));
    public static final ConfiguredFeature<?, ?> LIMBO_FABRIC_FLUID_ORE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "limbo_fabric_fluid_ore"), Feature.ORE.configure(new OreFeatureConfig(new BlockMatchRuleTest(ModBlocks.UNRAVELLED_FABRIC), ModBlocks.ETERNAL_FLUID.getDefaultState(), 6)).decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(0, 11, 9))));

    public static void init() {
        //just loads the class
    }
}
