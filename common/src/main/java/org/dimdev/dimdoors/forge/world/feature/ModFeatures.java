package org.dimdev.dimdoors.forge.world.feature;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
<<<<<<< HEAD:common/src/main/java/org/dimdev/dimdoors/forge/world/feature/ModFeatures.java
import net.minecraft.core.Registry;
=======
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
>>>>>>> merge-branch:common/src/main/java/org/dimdev/dimdoors/world/feature/ModFeatures.java
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
<<<<<<< HEAD:common/src/main/java/org/dimdev/dimdoors/forge/world/feature/ModFeatures.java
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.forge.world.feature.gateway.LimboGatewayFeature;
import org.dimdev.dimdoors.forge.world.feature.gateway.schematic.*;
=======
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.fluid.ModFluids;

import java.util.List;
import java.util.stream.Stream;
>>>>>>> merge-branch:common/src/main/java/org/dimdev/dimdoors/world/feature/ModFeatures.java

import static org.dimdev.dimdoors.DimensionalDoors.id;

@SuppressWarnings("unused")
public final class ModFeatures {
<<<<<<< HEAD:common/src/main/java/org/dimdev/dimdoors/forge/world/feature/ModFeatures.java
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

=======
>>>>>>> merge-branch:common/src/main/java/org/dimdev/dimdoors/world/feature/ModFeatures.java
	public static final class Configured {
		public static final ResourceKey<ConfiguredFeature<?, ?>> SOLID_STATIC_ORE = of("solid_static_ore");
		public static final ResourceKey<ConfiguredFeature<?, ?>> DECAYED_BLOCK_ORE = of("decayed_block_ore");
		public static final ResourceKey<ConfiguredFeature<?, ?>> ETERNAL_FLUID_SPRING = of("eternal_fluid_spring");

		public static ResourceKey<ConfiguredFeature<?, ?>> of(String id) {
			return ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, id(id));
		}

		public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> bootstapContext) {
			bootstapContext.register(Configured.DECAYED_BLOCK_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(new BlockMatchTest(ModBlocks.UNRAVELLED_FABRIC.get()), ModBlocks.DECAYED_BLOCK.get().defaultBlockState())), 64, 0.0f)));
			bootstapContext.register(Configured.SOLID_STATIC_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(new BlockMatchTest(ModBlocks.UNRAVELLED_FABRIC.get()), ModBlocks.SOLID_STATIC.get().defaultBlockState())), 4, 0.0f)));
			bootstapContext.register(Configured.ETERNAL_FLUID_SPRING, new ConfiguredFeature<>(Feature.SPRING, new SpringConfiguration(ModFluids.ETERNAL_FLUID.get().defaultFluidState(), true, 1, 4, Placed.holderSet(ModBlocks.UNRAVELLED_FABRIC, ModBlocks.UNRAVELLED_BLOCK, ModBlocks.UNFOLDED_BLOCK, ModBlocks.UNWARPED_BLOCK))));
		}
	}

	public static class Placed {
		public static final ResourceKey<PlacedFeature> SOLID_STATIC_ORE = of("solid_static_ore");
		public static final ResourceKey<PlacedFeature> DECAYED_BLOCK_ORE = of("decayed_block_ore");
		public static final ResourceKey<PlacedFeature> ETERNAL_FLUID_SPRING = of("eternal_fluid_spring");

		public static void bootstrap(BootstapContext<PlacedFeature> bootstapContext) {
			var lookup = bootstapContext.lookup(Registries.CONFIGURED_FEATURE);

			bootstapContext.register(Placed.DECAYED_BLOCK_ORE, new PlacedFeature(lookup.getOrThrow(Configured.DECAYED_BLOCK_ORE), List.of(CountPlacement.of(4), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(79)), InSquarePlacement.spread(), BiomeFilter.biome())));
			bootstapContext.register(Placed.SOLID_STATIC_ORE, new PlacedFeature(lookup.getOrThrow(Configured.SOLID_STATIC_ORE), List.of(CountPlacement.of(3), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.belowTop(79)), InSquarePlacement.spread(), BiomeFilter.biome())));
			bootstapContext.register(Placed.ETERNAL_FLUID_SPRING, new PlacedFeature(lookup.getOrThrow(Configured.ETERNAL_FLUID_SPRING), List.of(CountPlacement.of(3), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(192)), InSquarePlacement.spread(), BiomeFilter.biome())));
		}

		public static ResourceKey<PlacedFeature> of(String id) {
			return ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, id(id));
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
