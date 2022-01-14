package org.dimdev.dimdoors.world.feature;

import com.google.common.collect.ImmutableSet;
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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.BiomePlacementModifier;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.RarityFilterPlacementModifier;
import net.minecraft.world.gen.decorator.SquarePlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.SpringFeatureConfig;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;

public final class ModFeatures {
	public static final Feature<SchematicGatewayFeatureConfig> SCHEMATIC_GATEWAY_FEATURE;
	public static final Feature<DefaultFeatureConfig> LIMBO_GATEWAY_FEATURE;
	public static final SchematicGateway SANDSTONE_PILLARS_GATEWAY;
	public static final SchematicGateway TWO_PILLARS_GATEWAY = new TwoPillarsGateway();

	public static void init() {
		SANDSTONE_PILLARS_GATEWAY.init();
		TWO_PILLARS_GATEWAY.init();
		Registry.register(Registry.FEATURE, new Identifier("dimdoors", "schematic_gateway"), SCHEMATIC_GATEWAY_FEATURE);
		Configured.init();
		Placed.init();
	}

	static {
		SCHEMATIC_GATEWAY_FEATURE = new SchematicGatewayFeature(SchematicGatewayFeatureConfig.CODEC);
		SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsGateway();
		LIMBO_GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "limbo_gateway"), new LimboGatewayFeature());
	}

	public static final class Configured {
		public static final ConfiguredFeature<?, ?> SANDSTONE_PILLARS_CONFIGURED_FEATURE;
		public static final ConfiguredFeature<?, ?> TWO_PILLARS_CONFIGURED_FEATURE;
		public static final ConfiguredFeature<?, ?> LIMBO_GATEWAY_CONFIGURED_FEATURE;
		public static final ConfiguredFeature<?, ?> SOLID_STATIC_ORE_CONFIGURED_FEATURE;
		public static final ConfiguredFeature<?, ?> DECAYED_BLOCK_ORE_CONFIGURED_FEATURE;
		public static final ConfiguredFeature<?, ?> ETERNAL_FLUID_SPRING_CONFIGURED_FEATURE;
		public static final RegistryKey<ConfiguredFeature<?, ?>> SANDSTONE_PILLARS_CONFIGURED_FEATURE_KEY;
		public static final RegistryKey<ConfiguredFeature<?, ?>> TWO_PILLARS_CONFIGURED_FEATURE_KEY;

		public static void init() {
			ConfiguredFeatures.register(Configured.SANDSTONE_PILLARS_CONFIGURED_FEATURE_KEY.getValue().toString(), Configured.SANDSTONE_PILLARS_CONFIGURED_FEATURE);
			ConfiguredFeatures.register(Configured.TWO_PILLARS_CONFIGURED_FEATURE_KEY.getValue().toString(), Configured.TWO_PILLARS_CONFIGURED_FEATURE);
			ConfiguredFeatures.register("dimdoors:eternal_fluid_spring", Configured.ETERNAL_FLUID_SPRING_CONFIGURED_FEATURE);
			ConfiguredFeatures.register("dimdoors:solid_static_ore", Configured.SOLID_STATIC_ORE_CONFIGURED_FEATURE);
			ConfiguredFeatures.register("dimdoors:decayed_block_ore", Configured.DECAYED_BLOCK_ORE_CONFIGURED_FEATURE);
		}

		static {
			SANDSTONE_PILLARS_CONFIGURED_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier("dimdoors", "sandstone_pillars"));
			TWO_PILLARS_CONFIGURED_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier("dimdoors", "two_pillars"));
			SANDSTONE_PILLARS_CONFIGURED_FEATURE = SCHEMATIC_GATEWAY_FEATURE.configure(new SchematicGatewayFeatureConfig(SchematicGateway.ID_SCHEMATIC_MAP.inverse().get(SANDSTONE_PILLARS_GATEWAY)));
			TWO_PILLARS_CONFIGURED_FEATURE = SCHEMATIC_GATEWAY_FEATURE.configure(new SchematicGatewayFeatureConfig(SchematicGateway.ID_SCHEMATIC_MAP.inverse().get(TWO_PILLARS_GATEWAY)));
			LIMBO_GATEWAY_CONFIGURED_FEATURE = LIMBO_GATEWAY_FEATURE.configure(DefaultFeatureConfig.INSTANCE);
			SOLID_STATIC_ORE_CONFIGURED_FEATURE = Feature.ORE.configure(new OreFeatureConfig(new BlockMatchRuleTest(ModBlocks.UNRAVELLED_FABRIC), ModBlocks.SOLID_STATIC.getDefaultState(), 4));
			DECAYED_BLOCK_ORE_CONFIGURED_FEATURE = Feature.ORE.configure(new OreFeatureConfig(new BlockMatchRuleTest(ModBlocks.UNRAVELLED_FABRIC), ModBlocks.DECAYED_BLOCK.getDefaultState(), 64));
			ETERNAL_FLUID_SPRING_CONFIGURED_FEATURE = Feature.SPRING_FEATURE.configure(new SpringFeatureConfig(Fluids.WATER.getDefaultState(), true, 4, 1, ImmutableSet.of(ModBlocks.UNRAVELLED_FABRIC, ModBlocks.UNRAVELLED_BLOCK, ModBlocks.UNFOLDED_BLOCK, ModBlocks.UNWARPED_BLOCK)));
		}
	}

	public static class Placed {
		public static final PlacedFeature SANDSTONE_PILLARS_PLACED_FEATURE;
		public static final PlacedFeature TWO_PILLARS_PLACED_FEATURE;
		public static final PlacedFeature LIMBO_GATEWAY_PLACED_FEATURE;
		public static final PlacedFeature SOLID_STATIC_ORE_PLACED_FEATURE;
		public static final PlacedFeature DECAYED_BLOCK_ORE_PLACED_FEATURE;
		public static final PlacedFeature ETERNAL_FLUID_SPRING_PLACED_FEATURE;
		public static final RegistryKey<PlacedFeature> SANDSTONE_PILLARS_PLACED_FEATURE_KEY = RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier("dimdoors", "sandstone_pillars"));
		public static final RegistryKey<PlacedFeature> TWO_PILLARS_PLACED_FEATURE_KEY = RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier("dimdoors", "two_pillars"));

		public static void init() {
			BiomeModifications.addFeature(ctx -> {
						Biome biome = ctx.getBiome();
						return biome.getCategory() != Category.NONE &&
								biome.getCategory() != Category.THEEND &&
								biome.getCategory() != Category.NETHER &&
								biome.getCategory() != Category.OCEAN &&
								biome.getCategory() != Category.MUSHROOM &&
								biome.getCategory() != Category.DESERT;
						},
					GenerationStep.Feature.SURFACE_STRUCTURES,
					TWO_PILLARS_PLACED_FEATURE_KEY
			);
			BiomeModifications.addFeature(
					ctx -> ctx.getBiome().getCategory() == Category.DESERT,
					GenerationStep.Feature.SURFACE_STRUCTURES,
					SANDSTONE_PILLARS_PLACED_FEATURE_KEY
			);
			PlacedFeatures.register(SANDSTONE_PILLARS_PLACED_FEATURE_KEY.getValue().toString(), SANDSTONE_PILLARS_PLACED_FEATURE);
			PlacedFeatures.register(TWO_PILLARS_PLACED_FEATURE_KEY.getValue().toString(), TWO_PILLARS_PLACED_FEATURE);
			PlacedFeatures.register("dimdoors:eternal_fluid_spring", ETERNAL_FLUID_SPRING_PLACED_FEATURE);
			PlacedFeatures.register("dimdoors:solid_static_ore", SOLID_STATIC_ORE_PLACED_FEATURE);
			PlacedFeatures.register("dimdoors:decayed_block_ore", DECAYED_BLOCK_ORE_PLACED_FEATURE);
		}

		static {
			SANDSTONE_PILLARS_PLACED_FEATURE = Configured.SANDSTONE_PILLARS_CONFIGURED_FEATURE.withPlacement(RarityFilterPlacementModifier.of(DimensionalDoorsInitializer.getConfig().getWorldConfig().gatewayGenChance), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());
			TWO_PILLARS_PLACED_FEATURE = Configured.SANDSTONE_PILLARS_CONFIGURED_FEATURE.withPlacement(RarityFilterPlacementModifier.of(DimensionalDoorsInitializer.getConfig().getWorldConfig().gatewayGenChance), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());
			LIMBO_GATEWAY_PLACED_FEATURE = Configured.LIMBO_GATEWAY_CONFIGURED_FEATURE.withPlacement(RarityFilterPlacementModifier.of(DimensionalDoorsInitializer.getConfig().getWorldConfig().gatewayGenChance), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());
			SOLID_STATIC_ORE_PLACED_FEATURE = Configured.SOLID_STATIC_ORE_CONFIGURED_FEATURE.withPlacement(CountPlacementModifier.of(3), HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.getTop()), SquarePlacementModifier.of(), BiomePlacementModifier.of());
			DECAYED_BLOCK_ORE_PLACED_FEATURE = Configured.DECAYED_BLOCK_ORE_CONFIGURED_FEATURE.withPlacement(CountPlacementModifier.of(4), HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(79)), SquarePlacementModifier.of(), BiomePlacementModifier.of());
			ETERNAL_FLUID_SPRING_PLACED_FEATURE = Configured.ETERNAL_FLUID_SPRING_CONFIGURED_FEATURE.withPlacement(CountPlacementModifier.of(25), SquarePlacementModifier.of(), HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(192)), BiomePlacementModifier.of());
		}
	}
}
