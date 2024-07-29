package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalLong;

import static org.dimdev.dimdoors.world.ModDimensions.*;

public class DatagenInitializer implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {

		var pack = generator.createPack();

		pack.addProvider(DimDoorsModelProvider::new);
		pack.addProvider((DataProvider.Factory<DataProvider>) DimdoorsRecipeProvider::new);
		pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>) AdvancementProvider::new);
		pack.addProvider(org.dimdev.dimdoors.datagen.LootTableProvider::new);
		pack.addProvider((DataProvider.Factory<DataProvider>) org.dimdev.dimdoors.datagen.LimboDecayProvider::new);
		pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>) BlockTagProvider::new);
		pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>) ItemTagProvider::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.BIOME, ModBiomes::bootstrap)
				.add(Registries.CONFIGURED_FEATURE, new RegistrySetBuilder.RegistryBootstrap<ConfiguredFeature<?, ?>>() {
					@Override
					public void run(BootstapContext<ConfiguredFeature<?, ?>> context) {
						ModFeatures.Configured.init(context);
					}
				})
						.add(Registries.DIMENSION_TYPE, new RegistrySetBuilder.RegistryBootstrap<DimensionType>() {
							@Override
							public void run(BootstapContext<DimensionType> bootstapContext) {
								bootstapContext.register(LIMBO_TYPE_KEY, new DimensionType(OptionalLong.of(6000), true, false, false, false, 4, false, true, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, DimensionalDoors.id("limbo"), 0.1f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)));
								bootstapContext.register(POCKET_TYPE_KEY, new DimensionType(OptionalLong.of(6000), true, false, false, false, 4, false, true, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, DimensionalDoors.id("dungeon"), 0.1f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)));
							}
						})
						.add(Registries.LEVEL_STEM, new RegistrySetBuilder.RegistryBootstrap<LevelStem>() {
							@Override
							public void run(BootstapContext<LevelStem> bootstapContext) {
								var dimensionType = bootstapContext.lookup(Registries.DIMENSION_TYPE);
								var biomes = bootstapContext.lookup(Registries.BIOME);

								bootstapContext.register(LIMBO_STEM, new LevelStem(dimensionType.getOrThrow(LIMBO_TYPE_KEY), new NoiseBasedChunkGenerator(new FixedBiomeSource(biomes.getOrThrow(ModBiomes.LIMBO_KEY)), bootstapContext.lookup(Registries.NOISE_SETTINGS).getOrThrow(ModChunkGeneratorSettings.LIMBO))));
//        entries.register(PERSONAL_STEM, createPocketStem(pocketType, entries.ref(ModBiomes.PERSONAL_WHITE_VOID_KEY)));
//        entries.register(PUBLIC_STEM, createPocketStem(pocketType, entries.ref(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY)));
//        entries.register(DUNGEON_STEM, createPocketStem(pocketType, entries.ref(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY)));
							}
						})
				.add(Registries.DENSITY_FUNCTION, ModDensityFunctions::bootstrap)
								.add(Registries.NOISE, ModNoiseParameters::bootstrap)
				.add(Registries.NOISE_SETTINGS, ModChunkGeneratorSettings::bootstrap);
		//		ModCarvers.bootstrap(entries);
	}

	@Override
	public @Nullable String getEffectiveModId() {
		return DimensionalDoors.MOD_ID;
	}
}
