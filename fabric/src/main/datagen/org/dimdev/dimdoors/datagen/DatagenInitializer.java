package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.DimensionType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModGatewayPools;
import org.dimdev.dimdoors.world.ModProcessorLists;
import org.dimdev.dimdoors.world.ModStructures;
import org.dimdev.dimdoors.world.carvers.ModCarvers;
import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalLong;

import static org.dimdev.dimdoors.world.ModDimensions.LIMBO_TYPE_KEY;
import static org.dimdev.dimdoors.world.ModDimensions.POCKET_TYPE_KEY;

public class DatagenInitializer implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {

		var pack = generator.createPack();

		pack.addProvider(DimDoorsModelProvider::new);
		pack.addProvider((DataProvider.Factory<DataProvider>) DimdoorsRecipeProvider::new);
		pack.addProvider(AdvancementProvider::new);
		pack.addProvider(org.dimdev.dimdoors.datagen.LootTableProvider::new);
		pack.addProvider((DataProvider.Factory<DataProvider>) org.dimdev.dimdoors.datagen.LimboDecayProvider::new);
		pack.addProvider(BlockTagProvider::new);
		pack.addProvider(BiomeTagProvider::new);
		pack.addProvider(ItemTagProvider::new);
		pack.addProvider((output, registriesFuture) -> new FabricDynamicRegistryProvider(output, registriesFuture) {

			@Override
			public String getName() {
				return "DimDoors Worldgen";
			}

			@Override
			protected void configure(HolderLookup.Provider registries, Entries entries) {
				entries.addAll(registries.lookupOrThrow(Registries.BIOME));
				entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
				entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
				entries.addAll(registries.lookupOrThrow(Registries.DIMENSION_TYPE));
//				entries.addAll(registries.lookupOrThrow(Registries.LEVEL_STEM)); //TODO: Add when https://github.com/FabricMC/fabric/issues/3838 is resolved.

				entries.addAll(registries.lookupOrThrow(Registries.NOISE));
//				entries.addAll(registries.lookupOrThrow(Registries.NOISE_SETTINGS));
				entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_CARVER));

				entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE));
				entries.addAll(registries.lookupOrThrow(Registries.TEMPLATE_POOL));
				entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE_SET));
				entries.addAll(registries.lookupOrThrow(Registries.PROCESSOR_LIST));
			}
		});
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.BIOME, ModBiomes::bootstrap)
				.add(Registries.CONFIGURED_FEATURE, ModFeatures.Configured::bootstrap)
				.add(Registries.PLACED_FEATURE, ModFeatures.Placed::bootstrap)
				.add(Registries.DIMENSION_TYPE, bootstapContext -> {
					bootstapContext.register(LIMBO_TYPE_KEY, new DimensionType(OptionalLong.of(6000), true, false, false, false, 4, false, true, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, DimensionalDoors.id("limbo"), 0.1f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)));
					bootstapContext.register(POCKET_TYPE_KEY, new DimensionType(OptionalLong.of(6000), true, false, false, false, 4, false, true, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, DimensionalDoors.id("dungeon"), 0.1f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)));
				})
//				.add(Registries.LEVEL_STEM, bootstapContext -> { TODO: Finish and enable when https://github.com/FabricMC/fabric/issues/3838 is resolved
//							var dimensionType = bootstapContext.lookup(Registries.DIMENSION_TYPE);
//							var biomes = bootstapContext.lookup(Registries.BIOME);
//
//							bootstapContext.register(LIMBO_STEM, new LevelStem(dimensionType.getOrThrow(LIMBO_TYPE_KEY), new NoiseBasedChunkGenerator(new FixedBiomeSource(biomes.getOrThrow(ModBiomes.LIMBO_KEY)), bootstapContext.lookup(Registries.NOISE_SETTINGS).getOrThrow(ModChunkGeneratorSettings.LIMBO))));
//        entries.register(PERSONAL_STEM, createPocketStem(pocketType, entries.ref(ModBiomes.PERSONAL_WHITE_VOID_KEY)));
//        entries.register(PUBLIC_STEM, createPocketStem(pocketType, entries.ref(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY)));
//        entries.register(DUNGEON_STEM, createPocketStem(pocketType, entries.ref(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY)));
//						})
				.add(Registries.DENSITY_FUNCTION, ModDensityFunctions::bootstrap)
				.add(Registries.NOISE, ModNoiseParameters::bootstrap)
				.add(Registries.NOISE_SETTINGS, ModChunkGeneratorSettings::bootstrap)
				.add(Registries.CONFIGURED_CARVER, ModCarvers::bootstrap)
				.add(Registries.STRUCTURE, ModStructures::new)
				.add(Registries.TEMPLATE_POOL, ModGatewayPools::bootstrap)
				.add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap)
				.add(Registries.PROCESSOR_LIST, ModProcessorLists::bootstrap);

	}

	@Override
	public @Nullable String getEffectiveModId() {
		return DimensionalDoors.MOD_ID;
	}
}
