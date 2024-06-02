package org.dimdev.dimdoors.forge.world.feature;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.forge.world.feature.gateway.LimboGatewayFeature;
import org.dimdev.dimdoors.forge.world.feature.gateway.schematic.*;

import static org.dimdev.dimdoors.DimensionalDoors.id;

@SuppressWarnings("unused")
public final class ModFeatures {
	public static final SandstonePillarsGateway SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsGateway();
	public static final TwoPillarsGateway TWO_PILLARS_GATEWAY = new TwoPillarsGateway();
	public static final EndGateway END_GATEWAY = new EndGateway();

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(DimensionalDoors.MOD_ID, Registry.FEATURE_REGISTRY);

	public static final RegistrySupplier<Feature<SchematicGatewayFeatureConfig>> SANDSTONE_PILLARS_GATEWAY_FEATURE = FEATURES.register("two_pillars", () -> new SchematicGatewayFeature(SchematicGatewayFeatureConfig.CODEC));
	public static final RegistrySupplier<Feature<SchematicGatewayFeatureConfig>> TWO_PILLARS_GATEWAY_FEATURE = FEATURES.register("sandstone_pillars", () -> new SchematicGatewayFeature(SchematicGatewayFeatureConfig.CODEC));
	public static final RegistrySupplier<Feature<SchematicGatewayFeatureConfig>> END_GATEWAY_FEATURE = FEATURES.register("schematic_gateway", () -> new SchematicGatewayFeature(SchematicGatewayFeatureConfig.CODEC));
	public static final RegistrySupplier<Feature<NoneFeatureConfiguration>> LIMBO_GATEWAY_FEATURE = FEATURES.register("limbo_gateway", LimboGatewayFeature::new);

	public static void init() {
		LifecycleEvent.SETUP.register(() -> {
			SANDSTONE_PILLARS_GATEWAY.init();
			TWO_PILLARS_GATEWAY.init();
			END_GATEWAY.init();
		});

		FEATURES.register();

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
			return ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, id(id));
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
//			BiomeModifications.addProperties(context -> Placed.isOverworld(context) && !Placed.isDesert(context) && !Placed.isOcean(context), (context, mutable) -> mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, TWO_PILLARS));
//			BiomeModifications.addProperties(ModFeatures.Placed::isDesert, (context, mutable) -> mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, SANDSTONE_PILLARS));
//			BiomeModifications.addProperties(ModFeatures.Placed::isEnd, (context, mutable) -> mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, END_GATEWAY));

//			BiomeModifications.addFeature(ctx -> ctx.hasTag(ConventionalBiomeTags.IN_OVERWORLD) &&
//							!ctx.hasTag(ConventionalBiomeTags.DESERT) &&
//							!ctx.hasTag(ConventionalBiomeTags.OCEAN),
//					GenerationStep.Feature.SURFACE_STRUCTURES,
//					TWO_PILLARS
//			);
//			BiomeModifications.addFeature(
//					ctx -> ctx.hasTag(ConventionalBiomeTags.DESERT),
//					GenerationStep.Feature.SURFACE_STRUCTURES,
//					SANDSTONE_PILLARS
//			);
//
//			BiomeModifications.addFeature(
//					ctx -> !ctx.getBiomeKey().equals(BiomeKeys.THE_END) && ctx.hasTag(ConventionalBiomeTags.IN_THE_END),
//					GenerationStep.Feature.SURFACE_STRUCTURES,
//					END_GATEWAY
//			);
		}

		public static ResourceKey<PlacedFeature> of(String id) {
			return ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, id(id));
		}

		@ExpectPlatform
		public static boolean isDesert(BiomeModifications.BiomeContext context) {
			throw new RuntimeException();
		}

		@ExpectPlatform
		public static boolean isOcean(BiomeModifications.BiomeContext context) {
			throw new RuntimeException();
		}

		@ExpectPlatform
		public static boolean isEnd(BiomeModifications.BiomeContext context) {
			throw new RuntimeException();
		}

		@ExpectPlatform
		public static boolean isOverworld(BiomeModifications.BiomeContext context) {
			throw new RuntimeException();
		}
	}
}
