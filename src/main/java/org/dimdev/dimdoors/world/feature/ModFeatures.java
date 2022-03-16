package org.dimdev.dimdoors.world.feature;

import net.minecraft.block.Block;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.placementmodifier.*;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.feature.gateway.LimboGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SandstonePillarsGateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGatewayFeatureConfig;
import org.dimdev.dimdoors.world.feature.gateway.schematic.TwoPillarsGateway;

import net.minecraft.fluid.Fluids;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.SpringFeatureConfig;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;

import java.util.List;

import static net.minecraft.world.gen.feature.Feature.*;

public final class ModFeatures {
	public static final SchematicGateway SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsGateway();
	public static final SchematicGateway TWO_PILLARS_GATEWAY = new TwoPillarsGateway();

	public static final Feature<SchematicGatewayFeatureConfig> SCHEMATIC_GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "schematic_gateway"), new SchematicGatewayFeature(SchematicGatewayFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> LIMBO_GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "limbo_gateway"), new LimboGatewayFeature());

	public static void init() {
		SANDSTONE_PILLARS_GATEWAY.init();
		TWO_PILLARS_GATEWAY.init();
;
		Configured.init();
		Placed.init();
	}

	public static final class Configured {
		public static final RegistryEntry<ConfiguredFeature<SchematicGatewayFeatureConfig, ?>> SANDSTONE_PILLARS_CONFIGURED_FEATURE = ConfiguredFeatures.register("dimdoors:sandstone_pillars", SCHEMATIC_GATEWAY_FEATURE, new SchematicGatewayFeatureConfig(SchematicGateway.ID_SCHEMATIC_MAP.inverse().get(SANDSTONE_PILLARS_GATEWAY)));
		public static final RegistryEntry<ConfiguredFeature<SchematicGatewayFeatureConfig, ?>> TWO_PILLARS_CONFIGURED_FEATURE = ConfiguredFeatures.register("dimdoors:two_pillars", SCHEMATIC_GATEWAY_FEATURE, new SchematicGatewayFeatureConfig(SchematicGateway.ID_SCHEMATIC_MAP.inverse().get(TWO_PILLARS_GATEWAY)));
		public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> LIMBO_GATEWAY_CONFIGURED_FEATURE =  ConfiguredFeatures.register("dimdoors:limbo_gateway", LIMBO_GATEWAY_FEATURE, DefaultFeatureConfig.INSTANCE);
		public static final RegistryEntry<ConfiguredFeature<SpringFeatureConfig, ?>> SOLID_STATIC_ORE_CONFIGURED_FEATURE =  ConfiguredFeatures.register("dimdoors:eternal_fluid_spring", SPRING_FEATURE, new SpringFeatureConfig(Fluids.WATER.getDefaultState(), true, 4, 1, RegistryEntryList.of(Block::getRegistryEntry, ModBlocks.UNRAVELLED_FABRIC, ModBlocks.UNRAVELLED_BLOCK, ModBlocks.UNFOLDED_BLOCK, ModBlocks.UNWARPED_BLOCK)));
		public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> DECAYED_BLOCK_ORE_CONFIGURED_FEATURE =  ConfiguredFeatures.register("dimdoors:solid_static_ore", ORE, new OreFeatureConfig(new BlockMatchRuleTest(ModBlocks.UNRAVELLED_FABRIC), ModBlocks.SOLID_STATIC.getDefaultState(), 4));
		public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> ETERNAL_FLUID_SPRING_CONFIGURED_FEATURE =  ConfiguredFeatures.register("dimdoors:decayed_block_ore", ORE, new OreFeatureConfig(new BlockMatchRuleTest(ModBlocks.UNRAVELLED_FABRIC), ModBlocks.DECAYED_BLOCK.getDefaultState(), 64));

		public static void init() {}
	}

	public static class Placed {
		public static final RegistryEntry<PlacedFeature> SANDSTONE_PILLARS_PLACED_FEATURE = PlacedFeatures.register("dimdoors:sandstone_pillars", Configured.SANDSTONE_PILLARS_CONFIGURED_FEATURE, List.of(RarityFilterPlacementModifier.of(DimensionalDoorsInitializer.getConfig().getWorldConfig().gatewayGenChance), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of()));
		public static final RegistryEntry<PlacedFeature> TWO_PILLARS_PLACED_FEATURE = PlacedFeatures.register("dimdoors:two_pillars", Configured.SANDSTONE_PILLARS_CONFIGURED_FEATURE, List.of(RarityFilterPlacementModifier.of(DimensionalDoorsInitializer.getConfig().getWorldConfig().gatewayGenChance), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of()));
		public static final RegistryEntry<PlacedFeature> LIMBO_GATEWAY_PLACED_FEATURE = PlacedFeatures.register("dimdoors:limbo_gateway", Configured.LIMBO_GATEWAY_CONFIGURED_FEATURE, List.of(RarityFilterPlacementModifier.of(DimensionalDoorsInitializer.getConfig().getWorldConfig().gatewayGenChance), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
		public static final RegistryEntry<PlacedFeature> SOLID_STATIC_ORE_PLACED_FEATURE = PlacedFeatures.register("dimdoors:solid_static_ore", Configured.SOLID_STATIC_ORE_CONFIGURED_FEATURE, List.of(CountPlacementModifier.of(1), HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.getTop()), SquarePlacementModifier.of(), BiomePlacementModifier.of()));
		public static final RegistryEntry<PlacedFeature> DECAYED_BLOCK_ORE_PLACED_FEATURE = PlacedFeatures.register("dimdoors:decayed_block_ore", Configured.DECAYED_BLOCK_ORE_CONFIGURED_FEATURE, List.of(CountPlacementModifier.of(4), HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(79)), SquarePlacementModifier.of(), BiomePlacementModifier.of()));
		public static final RegistryEntry<PlacedFeature> ETERNAL_FLUID_SPRING_PLACED_FEATURE = PlacedFeatures.register("dimdoors:eternal_fluid_spring", Configured.ETERNAL_FLUID_SPRING_CONFIGURED_FEATURE, List.of(CountPlacementModifier.of(25), SquarePlacementModifier.of(), HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(192)), BiomePlacementModifier.of()));

		public static void init() {
			BiomeModifications.addFeature(ctx -> {
						Biome biome = ctx.getBiome();
						return biome.getCategory() != Category.NONE &&
								biome.getCategory() != Category.THEEND &&
								biome.getCategory() != Category.NETHER;
						},
					GenerationStep.Feature.SURFACE_STRUCTURES,
					TWO_PILLARS_PLACED_FEATURE.getKey().get()
			);
			BiomeModifications.addFeature(
					ctx -> ctx.getBiome().getCategory() == Category.DESERT,
					GenerationStep.Feature.SURFACE_STRUCTURES,
					SANDSTONE_PILLARS_PLACED_FEATURE.getKey().get()
			);
		}
	}
}
