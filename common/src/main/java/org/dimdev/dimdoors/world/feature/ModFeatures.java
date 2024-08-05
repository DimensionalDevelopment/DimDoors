package org.dimdev.dimdoors.world.feature;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.fluid.ModFluids;

import java.util.List;
import java.util.stream.Stream;

import static org.dimdev.dimdoors.DimensionalDoors.id;

@SuppressWarnings("unused")
public final class ModFeatures {
	public static final class Configured {
		public static final ResourceKey<ConfiguredFeature<?, ?>> SOLID_STATIC_ORE = of("solid_static_ore");
		public static final ResourceKey<ConfiguredFeature<?, ?>> DECAYED_BLOCK_ORE = of("decayed_block_ore");
		public static final ResourceKey<ConfiguredFeature<?, ?>> ETERNAL_FLUID_SPRING = of("eternal_fluid_spring");

		public static ResourceKey<ConfiguredFeature<?, ?>> of(String id) {
			return ResourceKey.create(Registries.CONFIGURED_FEATURE, id(id));
		}

		public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> bootstapContext) {
			bootstapContext.register(Configured.DECAYED_BLOCK_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(new BlockMatchTest(ModBlocks.UNRAVELLED_FABRIC.get()), ModBlocks.DECAYED_BLOCK.get().defaultBlockState())), 64, 0.0f)));
			bootstapContext.register(Configured.SOLID_STATIC_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(new BlockMatchTest(ModBlocks.UNRAVELLED_FABRIC.get()), ModBlocks.SOLID_STATIC.get().defaultBlockState())), 4, 0.0f)));
			bootstapContext.register(Configured.ETERNAL_FLUID_SPRING, new ConfiguredFeature<>(Feature.SPRING, new SpringConfiguration(ModFluids.ETERNAL_FLUID.get().defaultFluidState(), true, 1, 4, Placed.holderSet(ModBlocks.UNRAVELLED_FABRIC, ModBlocks.UNRAVELLED_BLOCK, ModBlocks.UNFOLDED_BLOCK, ModBlocks.UNWARPED_BLOCK))));
		}
	}

	public static class Placed {
		public static final ResourceKey<PlacedFeature> SOLID_STATIC_ORE = of("solid_static_ore");
		public static final ResourceKey<PlacedFeature> DECAYED_BLOCK_ORE = of("decayed_block_ore");
		public static final ResourceKey<PlacedFeature> ETERNAL_FLUID_SPRING = of("eternal_fluid_spring");

		public static void bootstrap(BootstrapContext<PlacedFeature> bootstapContext) {
			var lookup = bootstapContext.lookup(Registries.CONFIGURED_FEATURE);

			bootstapContext.register(Placed.DECAYED_BLOCK_ORE, new PlacedFeature(lookup.getOrThrow(Configured.DECAYED_BLOCK_ORE), List.of(CountPlacement.of(4), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(79)), InSquarePlacement.spread(), BiomeFilter.biome())));
			bootstapContext.register(Placed.SOLID_STATIC_ORE, new PlacedFeature(lookup.getOrThrow(Configured.SOLID_STATIC_ORE), List.of(CountPlacement.of(3), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.belowTop(79)), InSquarePlacement.spread(), BiomeFilter.biome())));
			bootstapContext.register(Placed.ETERNAL_FLUID_SPRING, new PlacedFeature(lookup.getOrThrow(Configured.ETERNAL_FLUID_SPRING), List.of(CountPlacement.of(3), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(192)), InSquarePlacement.spread(), BiomeFilter.biome())));
		}

		public static ResourceKey<PlacedFeature> of(String id) {
			return ResourceKey.create(Registries.PLACED_FEATURE, id(id));
		}

		@SafeVarargs
		public static HolderSet<Block> holderSet(RegistrySupplier<Block>... blocks) {
			return HolderSet.direct(Stream.of(blocks).map(DeferredSupplier::getKey).map(BuiltInRegistries.BLOCK::getHolderOrThrow).toList());
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
