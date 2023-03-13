package org.dimdev.dimdoors.world.feature;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.dimdev.dimdoors.world.feature.gateway.LimboGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.EndGateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SandstonePillarsGateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGatewayFeatureConfig;
import org.dimdev.dimdoors.world.feature.gateway.schematic.TwoPillarsGateway;

import static org.dimdev.dimdoors.DimensionalDoors.resource;

@SuppressWarnings("unused")
public final class ModFeatures {
	public static final SchematicGateway SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsGateway();
	public static final SchematicGateway TWO_PILLARS_GATEWAY = new TwoPillarsGateway();
	public static final SchematicGateway END_GATEWAY = new EndGateway();

	public static final Feature<SchematicGatewayFeatureConfig> SCHEMATIC_GATEWAY_FEATURE = Registry.register(BuiltInRegistries.FEATURE, resource("schematic_gateway"), new SchematicGatewayFeature(SchematicGatewayFeatureConfig.CODEC));
	public static final Feature<NoneFeatureConfiguration> LIMBO_GATEWAY_FEATURE = Registry.register(BuiltInRegistries.FEATURE, resource("limbo_gateway"), new LimboGatewayFeature());

	public static void init() {
		SANDSTONE_PILLARS_GATEWAY.init();
		TWO_PILLARS_GATEWAY.init();
		END_GATEWAY.init();

		Configured.init();
		Placed.init();
	}

	public static final class Configured {
		public static final ResourceKey<ConfiguredFeature<?, ?>> SANDSTONE_PILLARS = of("sandstone_pillars");
		public static final ResourceKey<ConfiguredFeature<?, ?>> TWO_PILLARS = of("two_pillars");
		public static final ResourceKey<ConfiguredFeature<?, ?>> END_GATEWAY = of("end_gateway");
		public static final ResourceKey<ConfiguredFeature<?, ?>> LIMBO_GATEWAY = of("limbo_gateway");
		public static final ResourceKey<ConfiguredFeature<?, ?>> SOLID_STATIC_ORE = of("solid_static_ore");
		public static final ResourceKey<ConfiguredFeature<?, ?>> DECAYED_BLOCK_ORE = of("decayed_block_ore");
		public static final ResourceKey<ConfiguredFeature<?, ?>> ETERNAL_FLUID_SPRING = of("eternal_fluid_spring");

		public static void init() {}

		public static ResourceKey<ConfiguredFeature<?, ?>> of(String id) {
			return ResourceKey.create(Registries.CONFIGURED_FEATURE, resource(id));
		}
	}

	public static class Placed {
		public static final ResourceKey<PlacedFeature> SANDSTONE_PILLARS = of("sandstone_pillars");
		public static final ResourceKey<PlacedFeature> TWO_PILLARS = of("two_pillars");
		public static final ResourceKey<PlacedFeature> END_GATEWAY = of("end_gateway");
		public static final ResourceKey<PlacedFeature> LIMBO_GATEWAY = of("limbo_gateway");
		public static final ResourceKey<PlacedFeature> SOLID_STATIC_ORE = of("solid_static_ore");
		public static final ResourceKey<PlacedFeature> DECAYED_BLOCK_ORE = of("decayed_block_ore");
		public static final ResourceKey<PlacedFeature> ETERNAL_FLUID_SPRING = of("eternal_fluid_spring");

		public static void init() {
			BiomeModifications.addFeature(ctx -> ctx.hasTag(ConventionalBiomeTags.IN_OVERWORLD) &&
					!ctx.hasTag(ConventionalBiomeTags.DESERT) &&
					!ctx.hasTag(ConventionalBiomeTags.OCEAN),
					GenerationStep.Decoration.SURFACE_STRUCTURES,
					TWO_PILLARS
			);
			BiomeModifications.addFeature(
					ctx -> ctx.hasTag(ConventionalBiomeTags.DESERT),
					GenerationStep.Decoration.SURFACE_STRUCTURES,
					SANDSTONE_PILLARS
			);

			BiomeModifications.addFeature(
					ctx -> !ctx.getBiomeKey().equals(Biomes.THE_END) && ctx.hasTag(ConventionalBiomeTags.IN_THE_END),
					GenerationStep.Decoration.SURFACE_STRUCTURES,
					END_GATEWAY
			);
		}

		public static ResourceKey<PlacedFeature> of(String id) {
			return ResourceKey.create(Registries.PLACED_FEATURE, resource(id));
		}
	}
}
