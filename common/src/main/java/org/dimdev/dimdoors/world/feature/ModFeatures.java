package org.dimdev.dimdoors.world.feature;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;

import org.dimdev.dimdoors.world.feature.gateway.LimboGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.EndGateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SandstonePillarsGateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGatewayFeatureConfig;
import org.dimdev.dimdoors.world.feature.gateway.schematic.TwoPillarsGateway;

import static org.dimdev.dimdoors.DimensionalDoors.id;

@SuppressWarnings("unused")
public final class ModFeatures {
	public static final SchematicGateway SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsGateway();
	public static final SchematicGateway TWO_PILLARS_GATEWAY = new TwoPillarsGateway();
	public static final SchematicGateway END_GATEWAY = new EndGateway();

	public static final Feature<SchematicGatewayFeatureConfig> SCHEMATIC_GATEWAY_FEATURE = Registry.register(Registries.FEATURE, id("schematic_gateway"), new SchematicGatewayFeature(SchematicGatewayFeatureConfig.CODEC));
	public static final Feature<DefaultFeatureConfig> LIMBO_GATEWAY_FEATURE = Registry.register(Registries.FEATURE, id("limbo_gateway"), new LimboGatewayFeature());

	public static void init() {
		SANDSTONE_PILLARS_GATEWAY.init();
		TWO_PILLARS_GATEWAY.init();
		END_GATEWAY.init();

		Configured.init();
		Placed.init();
	}

	public static final class Configured {
		public static final RegistryKey<ConfiguredFeature<?, ?>> SANDSTONE_PILLARS = of("sandstone_pillars");
		public static final RegistryKey<ConfiguredFeature<?, ?>> TWO_PILLARS = of("two_pillars");
		public static final RegistryKey<ConfiguredFeature<?, ?>> END_GATEWAY = of("end_gateway");
		public static final RegistryKey<ConfiguredFeature<?, ?>> LIMBO_GATEWAY = of("limbo_gateway");
		public static final RegistryKey<ConfiguredFeature<?, ?>> SOLID_STATIC_ORE = of("solid_static_ore");
		public static final RegistryKey<ConfiguredFeature<?, ?>> DECAYED_BLOCK_ORE = of("decayed_block_ore");
		public static final RegistryKey<ConfiguredFeature<?, ?>> ETERNAL_FLUID_SPRING = of("eternal_fluid_spring");

		public static void init() {}

		public static RegistryKey<ConfiguredFeature<?, ?>> of(String id) {
			return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, id(id));
		}
	}

	public static class Placed {
		public static final RegistryKey<PlacedFeature> SANDSTONE_PILLARS = of("sandstone_pillars");
		public static final RegistryKey<PlacedFeature> TWO_PILLARS = of("two_pillars");
		public static final RegistryKey<PlacedFeature> END_GATEWAY = of("end_gateway");
		public static final RegistryKey<PlacedFeature> LIMBO_GATEWAY = of("limbo_gateway");
		public static final RegistryKey<PlacedFeature> SOLID_STATIC_ORE = of("solid_static_ore");
		public static final RegistryKey<PlacedFeature> DECAYED_BLOCK_ORE = of("decayed_block_ore");
		public static final RegistryKey<PlacedFeature> ETERNAL_FLUID_SPRING = of("eternal_fluid_spring");

		public static void init() {
			BiomeModifications.addFeature(ctx -> ctx.hasTag(ConventionalBiomeTags.IN_OVERWORLD) &&
					!ctx.hasTag(ConventionalBiomeTags.DESERT) &&
					!ctx.hasTag(ConventionalBiomeTags.OCEAN),
					GenerationStep.Feature.SURFACE_STRUCTURES,
					TWO_PILLARS
			);
			BiomeModifications.addFeature(
					ctx -> ctx.hasTag(ConventionalBiomeTags.DESERT),
					GenerationStep.Feature.SURFACE_STRUCTURES,
					SANDSTONE_PILLARS
			);

			BiomeModifications.addFeature(
					ctx -> !ctx.getBiomeKey().equals(BiomeKeys.THE_END) && ctx.hasTag(ConventionalBiomeTags.IN_THE_END),
					GenerationStep.Feature.SURFACE_STRUCTURES,
					END_GATEWAY
			);
		}

		public static RegistryKey<PlacedFeature> of(String id) {
			return RegistryKey.of(RegistryKeys.PLACED_FEATURE, id(id));
		}
	}
}
