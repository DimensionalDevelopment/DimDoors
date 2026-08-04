package org.dimdev.dimdoors.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.PlaySoundEffect;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.enchantment.ModEnchants;
import org.dimdev.dimdoors.enchantment.effect.TranscendentProjectileEffect;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.ModJukeboxSongs;
import org.dimdev.dimdoors.item.loot.EntityNearBy;
import org.dimdev.dimdoors.painting.ModPaintings;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.tag.ModBiomeTags;
import org.dimdev.dimdoors.tag.ModEntityTypeTags;
import org.dimdev.dimdoors.tag.ModItemTags;
import org.dimdev.dimdoors.world.ModGatewayPools;
import org.dimdev.dimdoors.world.ModProcessorLists;
import org.dimdev.dimdoors.world.ModStructures;
import org.dimdev.dimdoors.world.carvers.ModCarvers;
import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.dimdev.dimdoors.world.structure.processors.DestinationDataModifier;

import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Stream;

import static net.minecraft.data.worldgen.Pools.EMPTY;
import static org.dimdev.dimdoors.world.ModBiomes.*;
import static org.dimdev.dimdoors.world.ModDimensions.*;
import static org.dimdev.dimdoors.world.carvers.ModCarvers.LIMBO_CARVER;

public class DefaultDynamicRegistryDataGen {
    public static void bootstrapConfiguredFeature(DimDoorsDynamicRegistryDatagen.RegistrationHelper entries) {
        entries.register(ModFeatures.Configured.DECAYED_BLOCK_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(new BlockMatchTest(ModBlocks.UNRAVELLED_FABRIC), ModBlocks.DECAYED_BLOCK.defaultBlockState())), 64, 0.0f)));
        entries.register(ModFeatures.Configured.SOLID_STATIC_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(new BlockMatchTest(ModBlocks.UNRAVELLED_FABRIC), ModBlocks.SOLID_STATIC.defaultBlockState())), 4, 0.0f)));
        entries.register(ModFeatures.Configured.ETERNAL_FLUID_SPRING, new ConfiguredFeature<>(Feature.SPRING, new SpringConfiguration(ModFluids.ETERNAL_FLUID.defaultFluidState(), true, 1, 4, blockSet(entries, ModBlocks.UNRAVELLED_FABRIC, ModBlocks.UNRAVELLED_BLOCK, ModBlocks.UNFOLDED_BLOCK, ModBlocks.UNWARPED_BLOCK))));
        entries.register(ModFeatures.Configured.DRIFTWOOD_TREE, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.DRIFTWOOD_LOG), new StraightTrunkPlacer(4, 2, 0), BlockStateProvider.simple(ModBlocks.DRIFTWOOD_LEAVES), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build()));
    }

    public static void bootstrapPlacedFeature(DimDoorsDynamicRegistryDatagen.RegistrationHelper entries) {
        entries.register(ModFeatures.Placed.DECAYED_BLOCK_ORE, new PlacedFeature(entries.lookup(ModFeatures.Configured.DECAYED_BLOCK_ORE), List.of(CountPlacement.of(4), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(79)), InSquarePlacement.spread(), BiomeFilter.biome())));
        entries.register(ModFeatures.Placed.SOLID_STATIC_ORE, new PlacedFeature(entries.lookup(ModFeatures.Configured.SOLID_STATIC_ORE), List.of(CountPlacement.of(3), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.belowTop(79)), InSquarePlacement.spread(), BiomeFilter.biome())));
        entries.register(ModFeatures.Placed.ETERNAL_FLUID_SPRING, new PlacedFeature(entries.lookup(ModFeatures.Configured.ETERNAL_FLUID_SPRING), List.of(CountPlacement.of(3), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(192)), InSquarePlacement.spread(), BiomeFilter.biome())));
    }

    public static void bootstrapDimensionType(DimDoorsDynamicRegistryDatagen.RegistrationHelper entries) {
        entries.register(LIMBO_TYPE_KEY, new DimensionType(OptionalLong.of(6000), true, false, false, false, 4, false, true, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, DimensionalDoors.id("limbo"), 0.1f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)));
        entries.register(POCKET_TYPE_KEY, new DimensionType(OptionalLong.of(6000), true, false, false, false, 4, false, true, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, DimensionalDoors.id("dungeon"), 0.1f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)));
    }

    public static void bootstrapLevelStem(DimDoorsDynamicRegistryDatagen.RegistrationHelper entries) {
//        TODO: Finish and enable when https://github.com/FabricMC/fabric/issues/3838 is resolved
//        var dimensionType = ctx.lookup(Registries.DIMENSION_TYPE);
//        var biomes = ctx.lookup(Registries.BIOME);
//
//        ctx.register(LIMBO_STEM, new LevelStem(dimensionType.getOrThrow(LIMBO_TYPE_KEY), new NoiseBasedChunkGenerator(new FixedBiomeSource(biomes.getOrThrow(ModBiomes.LIMBO_KEY)), ctx.lookup(Registries.NOISE_SETTINGS).getOrThrow(ModChunkGeneratorSettings.LIMBO))));
//        ctx.register(PERSONAL_STEM, new LevelStem(dimensionType.getOrThrow(POCKET_TYPE_KEY), BlankChunkGenerator.of(new FixedBiomeSource(biomes.getOrThrow(ModBiomes.PERSONAL_WHITE_VOID_KEY))));
//        ctx.register(PUBLIC_STEM, new LevelStem(dimensionType.getOrThrow(POCKET_TYPE_KEY), BlankChunkGenerator.of(new FixedBiomeSource(biomes.getOrThrow(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY))));
//        ctx.register(DUNGEON_STEM, new LevelStem(dimensionType.getOrThrow(POCKET_TYPE_KEY), BlankChunkGenerator.of(new FixedBiomeSource(biomes.getOrThrow(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY))));
    }

    public static void bootstrapBiomes(DimDoorsDynamicRegistryDatagen.RegistrationHelper entries) {
        entries.register(LIMBO_KEY, new Biome.BiomeBuilder()
                .downfall(0.0f).hasPrecipitation(false)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .temperature(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0x404040)
                        .waterColor(0x101010)
                        .waterFogColor(0)
                        .foliageColorOverride(0)
                        .skyColor(0x404040)
                        .grassColorOverride(0x404040)
                        .ambientMoodSound(new AmbientMoodSettings(
                                sound(entries, ModSoundEvents.CRACK),
                                6000,
                                8,
                                2
                        ))
                        .backgroundMusic(new Music(
                                sound(entries, ModSoundEvents.CREEPY),
                                0,
                                120000,
                                true
                        ))
                        .ambientParticle(new AmbientParticleSettings(
                                ModParticleTypes.LIMBO_ASH,
                                0.118093334f
                        )).build())
                .generationSettings(new BiomeGenerationSettings.PlainBuilder()
                        .addCarver(GenerationStep.Carving.AIR, entries.lookup(ModCarvers.LIMBO))
                        .addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, entries.lookup(ModFeatures.Placed.SOLID_STATIC_ORE))
                        .build())
                .mobSpawnSettings(new MobSpawnSettings.Builder()
                        .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(
                                ModEntityTypes.MONOLITH,
                                100,
                                1,
                                10
                        )).build())
                .build());
        var voidBiome = new Biome.BiomeBuilder()
                .downfall(0)
                .temperature(0.8f)
                .hasPrecipitation(false)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3f76e4)
                        .waterFogColor(0x50533)
                        .fogColor(0)
                        .skyColor(0)
                        .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY);

        entries.register(PUBLIC_BLACK_VOID_KEY, voidBiome.build());
        entries.register(DUNGEON_DANGEROUS_BLACK_VOID_KEY, voidBiome.build());

        entries.register(PERSONAL_WHITE_VOID_KEY, new Biome.BiomeBuilder()
                .downfall(0)
                .temperature(0.8f)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .hasPrecipitation(false)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3f76e4)
                        .waterFogColor(0x50533)
                        .fogColor(0xffffff)
                        .skyColor(0xffffff)
                        .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
                        .backgroundMusic(
                                new Music(
                                        sound(entries, ModSoundEvents.WHITE_VOID),
                                        0,
                                        0,
                                        true
                                )).build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build());
    }

    public static void bootstrapCarvers(DimDoorsDynamicRegistryDatagen.RegistrationHelper entries) {
        entries.register(ModCarvers.LIMBO, new ConfiguredWorldCarver<>(LIMBO_CARVER, new CaveCarverConfiguration(
                0.2f,
                UniformHeight.of(VerticalAnchor.aboveBottom(0), VerticalAnchor.belowTop(8)),
                ConstantFloat.of(0.5f),
                VerticalAnchor.aboveBottom(10),
                CarverDebugSettings.DEFAULT,
                blockSet(entries, ModBlocks.UNRAVELLED_FABRIC),
                ConstantFloat.of(1),
                ConstantFloat.of(1),
                ConstantFloat.of(-0.7f)
        )));
    }

    public static void bootstrapStructures(DimDoorsDynamicRegistryDatagen.RegistrationHelper context) {
        var biomes = context.registrylookup(Registries.BIOME);
        var pools = context.registrylookup(Registries.TEMPLATE_POOL);

        registerStructure(context, biomes, pools, ModStructures.ENCLOSED_GATEWAY, ModBiomeTags.ENCLOSED_GATEWAY, ModGatewayPools.ENCLOSED_GATEWAY);
        registerStructure(context, biomes, pools, ModStructures.ENCLOSED_ENDSTONE_GATEWAY, ModBiomeTags.ENCLOSED_ENDSTONE_GATEWAY, ModGatewayPools.ENCLOSED_ENDSTONE_GATEWAY);
        registerStructure(context, biomes, pools, ModStructures.ENCLOSED_MUD_GATEWAY, ModBiomeTags.ENCLOSED_MUD_GATEWAY, ModGatewayPools.ENCLOSED_MUD_GATEWAY);
        context.register(ModStructures.ENCLOSED_PRISMARINE_GATEWAY, new JigsawStructure(new Structure.StructureSettings(biomes.getOrThrow(ModBiomeTags.ENCLOSED_PRISMARINE_GATEWAY), Collections.emptyMap(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN), pools.getOrThrow(ModGatewayPools.ENCLOSED_PRISMARINE_GATEWAY), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.OCEAN_FLOOR_WG));
        registerStructure(context, biomes, pools, ModStructures.ENCLOSED_QUARTZ_GATEWAY, ModBiomeTags.ENCLOSED_QUARTZ_GATEWAY, ModGatewayPools.ENCLOSED_QUARTZ_GATEWAY);
        registerStructure(context, biomes, pools, ModStructures.ENCLOSED_RED_SANDSTONE_GATEWAY, ModBiomeTags.ENCLOSED_RED_SANDSTONE_GATEWAY, ModGatewayPools.ENCLOSED_RED_SANDSTONE_GATEWAY);
        registerStructure(context, biomes, pools, ModStructures.ENCLOSED_SANDSTONE_GATEWAY, ModBiomeTags.ENCLOSED_SANDSTONE_GATEWAY, ModGatewayPools.ENCLOSED_SANDSTONE_GATEWAY);
//        register(LIMBO_GATEWAY, ModBiomeTags.LIMBO_GATEWAY, ModGatewayPools.LIMBO_GATEWAY);

//        register(TWO_PILLARS, ModBiomeTags.TWO_PILLARS, ModGatewayPools.TWO_PILLARS);
//        register(SANDSTONE_PILLARS, ModBiomeTags.SANDSTONE_PILLARS, ModGatewayPools.SANDSTONE_PILLARS);
//        register(RED_SANDSTONE_PILLARS, ModBiomeTags.RED_SANDSTONE_PILLARS, ModGatewayPools.RED_SANDSTONE_PILLARS);
//        register(ICE_PILLARS, ModBiomeTags.ICE_PILLARS, ModGatewayPools.ICE_PILLARS);
    }

    private static void registerStructure(DimDoorsDynamicRegistryDatagen.RegistrationHelper context, HolderLookup<Biome> biomes, HolderLookup<StructureTemplatePool> pools, ResourceKey<Structure> structure, TagKey<Biome> biome, ResourceKey<StructureTemplatePool> pool) {
        context.register(structure, new JigsawStructure(new Structure.StructureSettings(biomes.getOrThrow(biome), Collections.emptyMap(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN), pools.getOrThrow(pool), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
    }

    public static void bootstrapProcessorLists(DimDoorsDynamicRegistryDatagen.RegistrationHelper context) {
        context.register(ModProcessorLists.DUNGEON, new StructureProcessorList(List.of(DestinationDataModifier.of(DefaultDungeonDestinations.getShallowerDungeonDestination()))));
    }

    public static void bootstrapGatewayPools(DimDoorsDynamicRegistryProvider.RegistrationHelper context) {
        var empty = context.lookup(EMPTY);
        var processorLists = context.registrylookup(Registries.PROCESSOR_LIST);

        var dungeon = processorLists.getOrThrow(ModProcessorLists.DUNGEON);
        context.register(ModGatewayPools.ENCLOSED_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ModGatewayPools.ENCLOSED_ENDSTONE_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_endstone", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ModGatewayPools.ENCLOSED_MUD_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_mud", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ModGatewayPools.ENCLOSED_PRISMARINE_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_prismarine", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ModGatewayPools.ENCLOSED_QUARTZ_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_quartz", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ModGatewayPools.ENCLOSED_RED_SANDSTONE_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_red_sandstone", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ModGatewayPools.ENCLOSED_SANDSTONE_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_sandstone", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(LIMBO_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/limbo", dungeon), 50)), StructureTemplatePool.Projection.RIGID));

//        context.register(TWO_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/two_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(SANDSTONE_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(RED_SANDSTONE_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/red_sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(ICE_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/red_sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
    }

    public static void bootstrapStructureSets(DimDoorsDynamicRegistryProvider.RegistrationHelper context) {
        context.register(ModStructureSets.GATEWAYS, new StructureSet(

                List.of(new StructureSet.StructureSelectionEntry(context.lookup(ModStructures.ENCLOSED_GATEWAY), 1),
//                        new StructureSet.StructureSelectionEntry(context.lookup(ModStructures.ENCLOSED_ENDSTONE_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(ModStructures.ENCLOSED_MUD_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(ModStructures.ENCLOSED_PRISMARINE_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(ModStructures.ENCLOSED_QUARTZ_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(ModStructures.ENCLOSED_RED_SANDSTONE_GATEWAY), 1),
                        new StructureSet.StructureSelectionEntry(context.lookup(ModStructures.ENCLOSED_SANDSTONE_GATEWAY), 1)/*,
                        new StructureSet.StructureSelectionEntry(context.lookup(ModStructures.LIMBO_GATEWAY), 1)*/),
                new RandomSpreadStructurePlacement(
                        15,
                        5,
                        RandomSpreadType.TRIANGULAR,
                        23165478)));
    }

    public static void bootstrapJukeboxSongs(DimDoorsDynamicRegistryProvider.RegistrationHelper context) {
        context.register(ModJukeboxSongs.CREEPY, new JukeboxSong(sound(context, ModSoundEvents.CREEPY), Component.translatable("item.dimdoors.creepy_record.desc"), 317, 10));
        context.register(ModJukeboxSongs.WHITE_VOID, new JukeboxSong(sound(context, ModSoundEvents.WHITE_VOID), Component.translatable("item.dimdoors.white_void_record.desc"), 225, 10));
        context.register(ModJukeboxSongs.THEY_STARE_BACK, new JukeboxSong(sound(context, ModSoundEvents.THEY_STARE_BACK), Component.translatable("item.dimdoors.they_stare_back_record.desc"), 226, 10));
    }

    private static Holder<SoundEvent> sound(DimDoorsDynamicRegistryProvider.RegistrationHelper context, SoundEvent soundEvent) {
        return context.lookup(ResourceKey.create(Registries.SOUND_EVENT, soundEvent.getLocation()));
    }

    private static HolderSet<Block> blockSet(DimDoorsDynamicRegistryProvider.RegistrationHelper context, Block... blocks) {
        var blockLookup = context.registrylookup(Registries.BLOCK);
        return HolderSet.direct(Stream.of(blocks).map(block -> blockLookup.getOrThrow(block.builtInRegistryHolder().key())).toList());
    }

    public static void bootstrapEnchants(DimDoorsDynamicRegistryProvider.RegistrationHelper context) {
        HolderGetter<Enchantment> enchantments = context.registrylookup(Registries.ENCHANTMENT);
        HolderGetter<Item> items = context.registrylookup(Registries.ITEM);

        context.register(ModEnchants.STRING_THEORY_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE), 10, 4,
                        Enchantment.dynamicCost(1, 11),
                        Enchantment.dynamicCost(12, 11), 1,
                        EquipmentSlotGroup.ARMOR))
                .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE)).build(ModEnchants.STRING_THEORY_ENCHANTMENT.location()));

        context.register(ModEnchants.TREPIDATION_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE), 2, 3,
                        Enchantment.dynamicCost(5, 8),
                        Enchantment.dynamicCost(25, 8), 4,
                        EquipmentSlotGroup.CHEST))
                .withEffect(
                        EnchantmentEffectComponents.TICK,
                        new PlaySoundEffect(
                                Holder.direct(SoundEvents.WARDEN_HEARTBEAT),
                                ConstantFloat.of(0.8F),
                                ConstantFloat.of(1.0F)
                        ),
                        EntityNearBy.nearby(
                                EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(5.0F, 5.0F)),
                                EntityPredicate.Builder.entity()
                                        .entityType(new EntityTypePredicate(context.registrylookup(Registries.ENTITY_TYPE).getOrThrow(ModEntityTypeTags.TREPIDATION_DETECTED)))
                                        .build(),
                                40
                        )
                ).build(ModEnchants.TREPIDATION_ENCHANTMENT.location()));

        context.register(ModEnchants.TRANSCENDENT_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ModItemTags.TRANSCENDENT_ENCHANTABLE), 2, 1,
                        Enchantment.dynamicCost(15, 10),
                        Enchantment.dynamicCost(45, 10), 4,
                        EquipmentSlotGroup.MAINHAND))
                .withEffect(
                        EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                        TranscendentProjectileEffect.INSTANCE
                ).build(ModEnchants.TRANSCENDENT_ENCHANTMENT.location()));

        context.register(ModEnchants.RENDING_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.MINING_ENCHANTABLE), 2, 2,
                Enchantment.dynamicCost(15, 9),
                Enchantment.dynamicCost(45, 9), 4,
                EquipmentSlotGroup.MAINHAND)).build(ModEnchants.RENDING_ENCHANTMENT.location()));
    }


    public static void bootstrapPaintings(DimDoorsDynamicRegistryProvider.RegistrationHelper context) {
        registerPainting(context, ModPaintings.LIMBO, 4, 2);
        registerPainting(context, ModPaintings.PORTAL, 2, 4);
        registerPainting(context, ModPaintings.FREEDOM, 2, 2);
        registerPainting(context, ModPaintings.EYES, 2, 2);
        registerPainting(context, ModPaintings.GATEWAY_AT_NIGHT, 4, 2);


//        for (int index = 0; index < PAINTINGS_TO_DECAY_INTO.size(); index++) {
//            var x = index % 4;
//            var y = index / 4;
//
//            var key = PAINTINGS_TO_DECAY_INTO.get(index);
//
//            if(key.location().getPath().startsWith("placeholder")) {
//                register(context, key, x+1, y+1);
//            }
//        }
    }

    private static void registerPainting(DimDoorsDynamicRegistryProvider.RegistrationHelper bootstrapContext, ResourceKey<PaintingVariant> resourceKey, int width, int height) {
        bootstrapContext.register(resourceKey, new PaintingVariant(width, height, resourceKey.location()));
    }
}
