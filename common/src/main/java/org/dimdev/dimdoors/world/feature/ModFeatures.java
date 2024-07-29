package org.dimdev.dimdoors.world.feature;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import org.dimdev.dimdoors.block.ModBlocks;

import java.util.List;

import static org.dimdev.dimdoors.DimensionalDoors.id;

@SuppressWarnings("unused")
public final class ModFeatures {
	public static final class Configured {
		public static final ResourceKey<ConfiguredFeature<?, ?>> SOLID_STATIC_ORE = of("solid_static_ore");
		public static final ResourceKey<ConfiguredFeature<?, ?>> DECAYED_BLOCK_ORE = of("decayed_block_ore");
		public static final ResourceKey<ConfiguredFeature<?, ?>> ETERNAL_FLUID_SPRING = of("eternal_fluid_spring");

		public static void init(BootstapContext<ConfiguredFeature<?, ?>> context) {
			context.register(DECAYED_BLOCK_ORE, new ConfiguredFeature<>(
					Feature.ORE,
					new OreConfiguration(
							List.of(
									OreConfiguration.target(
											new BlockMatchTest(ModBlocks.UNRAVELLED_FABRIC.get()),
											ModBlocks.DECAYED_BLOCK.get().defaultBlockState())),
							64, 0.0f)));
//			context.register(ETERNAL_FLUID_SPRING, )
		}

		public static ResourceKey<ConfiguredFeature<?, ?>> of(String id) {
			return ResourceKey.create(Registries.CONFIGURED_FEATURE, id(id));
		}
	}

	public static class Placed {
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
			return ResourceKey.create(Registries.PLACED_FEATURE, id(id));
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
