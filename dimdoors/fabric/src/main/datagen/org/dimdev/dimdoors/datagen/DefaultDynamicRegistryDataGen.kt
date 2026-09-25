package org.dimdev.dimdoors.datagen

import com.mojang.datafixers.util.Pair
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.EntityTypePredicate
import net.minecraft.core.Holder
import net.minecraft.core.HolderGetter
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.Pools
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.Music
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.BlockTags
import net.minecraft.tags.EnchantmentTags
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.util.valueproviders.ConstantFloat
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.decoration.PaintingVariant
import net.minecraft.world.item.Item
import net.minecraft.world.item.JukeboxSong
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents
import net.minecraft.world.item.enchantment.LevelBasedValue
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect
import net.minecraft.world.item.enchantment.effects.PlaySoundEffect
import net.minecraft.world.level.biome.*
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight
import net.minecraft.world.level.levelgen.placement.*
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureSet
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.enchantment.ModEnchants
import org.dimdev.dimdoors.enchantment.effect.TranscendentProjectileEffect
import org.dimdev.dimdoors.entity.ModEntityTypes
import org.dimdev.dimdoors.fluid.ModFluids
import org.dimdev.dimdoors.item.ModJukeboxSongs
import org.dimdev.dimdoors.item.loot.EntityNearBy
import org.dimdev.dimdoors.painting.ModPaintings
import org.dimdev.dimdoors.particle.ModParticleTypes
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.tag.ModBiomeTags
import org.dimdev.dimdoors.tag.ModEntityTypeTags
import org.dimdev.dimdoors.tag.ModItemTags
import org.dimdev.dimdoors.world.ModBiomes
import org.dimdev.dimdoors.world.ModDimensions.LIMBO_TYPE_KEY
import org.dimdev.dimdoors.world.ModDimensions.POCKET_TYPE_KEY
import org.dimdev.dimdoors.world.ModGatewayPools
import org.dimdev.dimdoors.world.ModProcessorLists
import org.dimdev.dimdoors.world.ModStructures
import org.dimdev.dimdoors.world.carvers.ModCarvers
import org.dimdev.dimdoors.world.feature.ModFeatures
import org.dimdev.dimdoors.world.structure.processors.DestinationDataModifier
import java.util.*
import java.util.function.Function
import java.util.stream.Stream

object DefaultDynamicRegistryDataGen {
    fun bootstrapConfiguredFeature(entries: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        entries.register(
            ModFeatures.Configured.DECAYED_BLOCK_ORE,
            ConfiguredFeature(
                Feature.ORE,
                OreConfiguration(
                    listOf<OreConfiguration.TargetBlockState>(
                        OreConfiguration.target(
                            BlockMatchTest(
                                ModBlocks.UNRAVELLED_FABRIC.value()
                            ), ModBlocks.DECAYED_BLOCK.value().defaultBlockState()
                        )
                    ), 64, 0.0f
                )
            )
        )
        entries.register(
            ModFeatures.Configured.SOLID_STATIC_ORE,
            ConfiguredFeature(
                Feature.ORE,
                OreConfiguration(
                    listOf<OreConfiguration.TargetBlockState>(
                        OreConfiguration.target(
                            BlockMatchTest(
                                ModBlocks.UNRAVELLED_FABRIC.value()
                            ), ModBlocks.SOLID_STATIC.value().defaultBlockState()
                        )
                    ), 4, 0.0f
                )
            )
        )
        entries.register(
            ModFeatures.Configured.ETERNAL_FLUID_SPRING,
            ConfiguredFeature(
                Feature.SPRING,
                SpringConfiguration(
                    ModFluids.ETERNAL_FLUID.value().defaultFluidState(),
                    true,
                    1,
                    4,
                    blockSet(
                        entries,
                        ModBlocks.UNRAVELLED_FABRIC.value(),
                        ModBlocks.UNRAVELLED_BLOCK.value(),
                        ModBlocks.UNFOLDED_BLOCK.value(),
                        ModBlocks.UNWARPED_BLOCK.value()
                    )
                )
            )
        )
        entries.register(
            ModFeatures.Configured.DRIFTWOOD_TREE,
            ConfiguredFeature(
                Feature.TREE,
                TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(ModBlocks.DRIFTWOOD_LOG.value()),
                    StraightTrunkPlacer(4, 2, 0),
                    BlockStateProvider.simple(ModBlocks.DRIFTWOOD_LEAVES.value()),
                    BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                    TwoLayersFeatureSize(1, 0, 1)
                ).ignoreVines().build()
            )
        )
    }

    fun bootstrapPlacedFeature(entries: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        entries.register(
            ModFeatures.Placed.DECAYED_BLOCK_ORE,
            PlacedFeature(
                entries.lookup(ModFeatures.Configured.DECAYED_BLOCK_ORE),
                listOf<PlacementModifier?>(
                    CountPlacement.of(4),
                    HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(79)),
                    InSquarePlacement.spread(),
                    BiomeFilter.biome()
                )
            )
        )
        entries.register(
            ModFeatures.Placed.SOLID_STATIC_ORE,
            PlacedFeature(
                entries.lookup(ModFeatures.Configured.SOLID_STATIC_ORE),
                listOf<PlacementModifier?>(
                    CountPlacement.of(3),
                    HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.belowTop(79)),
                    InSquarePlacement.spread(),
                    BiomeFilter.biome()
                )
            )
        )
        entries.register(
            ModFeatures.Placed.ETERNAL_FLUID_SPRING,
            PlacedFeature(
                entries.lookup(ModFeatures.Configured.ETERNAL_FLUID_SPRING),
                listOf<PlacementModifier?>(
                    CountPlacement.of(3),
                    HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(192)),
                    InSquarePlacement.spread(),
                    BiomeFilter.biome()
                )
            )
        )
    }

    fun bootstrapDimensionType(entries: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        entries.register(
            LIMBO_TYPE_KEY,
            DimensionType(
                OptionalLong.of(6000),
                true,
                false,
                false,
                false,
                4.0,
                false,
                true,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                DimensionalDoors.id("limbo"),
                0.1f,
                DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)
            )
        )
        entries.register(
            POCKET_TYPE_KEY,
            DimensionType(
                OptionalLong.of(6000),
                true,
                false,
                false,
                false,
                4.0,
                false,
                true,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                DimensionalDoors.id("dungeon"),
                0.1f,
                DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)
            )
        )
    }

    fun bootstrapLevelStem(entries: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
//        TODO: Finish and enable when https://github.com/FabricMC/fabric/issues/3838 is resolved
//        var dimensionType = ctx.lookup(Registries.DIMENSION_TYPE);
//        var biomes = ctx.lookup(Registries.BIOME);
//
//        ctx.register(LIMBO_STEM, new LevelStem(dimensionType.getOrThrow(LIMBO_TYPE_KEY), new NoiseBasedChunkGenerator(new FixedBiomeSource(biomes.getOrThrow(ModBiomes.LIMBO_KEY)), ctx.lookup(Registries.NOISE_SETTINGS).getOrThrow(ModChunkGeneratorSettings.LIMBO))));
//        ctx.register(PERSONAL_STEM, new LevelStem(dimensionType.getOrThrow(POCKET_TYPE_KEY), BlankChunkGenerator.of(new FixedBiomeSource(biomes.getOrThrow(ModBiomes.PERSONAL_WHITE_VOID_KEY))));
//        ctx.register(PUBLIC_STEM, new LevelStem(dimensionType.getOrThrow(POCKET_TYPE_KEY), BlankChunkGenerator.of(new FixedBiomeSource(biomes.getOrThrow(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY))));
//        ctx.register(DUNGEON_STEM, new LevelStem(dimensionType.getOrThrow(POCKET_TYPE_KEY), BlankChunkGenerator.of(new FixedBiomeSource(biomes.getOrThrow(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY))));
    }

    fun bootstrapBiomes(entries: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        entries.register(
            ModBiomes.LIMBO_KEY, Biome.BiomeBuilder()
                .downfall(0.0f).hasPrecipitation(false)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .temperature(0.8f)
                .specialEffects(
                    BiomeSpecialEffects.Builder()
                        .fogColor(0x404040)
                        .waterColor(0x101010)
                        .waterFogColor(0)
                        .foliageColorOverride(0)
                        .skyColor(0x404040)
                        .grassColorOverride(0x404040)
                        .ambientMoodSound(
                            AmbientMoodSettings(
                                sound(entries, ModSoundEvents.CRACK),
                                6000,
                                8,
                                2.0
                            )
                        )
                        .backgroundMusic(
                            Music(
                                sound(entries, ModSoundEvents.CREEPY),
                                0,
                                120000,
                                true
                            )
                        )
                        .ambientParticle(
                            AmbientParticleSettings(
                                ModParticleTypes.LIMBO_ASH,
                                0.118093334f
                            )
                        ).build()
                )
                .generationSettings(
                    BiomeGenerationSettings.PlainBuilder()
                        .addCarver(
                            GenerationStep.Carving.AIR,
                            entries.lookup<ConfiguredWorldCarver<*>?>(ModCarvers.LIMBO)
                        )
                        .addFeature(
                            GenerationStep.Decoration.UNDERGROUND_ORES,
                            entries.lookup<PlacedFeature?>(ModFeatures.Placed.SOLID_STATIC_ORE)
                        )
                        .build()
                )
                .mobSpawnSettings(
                    MobSpawnSettings.Builder()
                        .addSpawn(
                            MobCategory.MONSTER, MobSpawnSettings.SpawnerData(
                                ModEntityTypes.MONOLITH.value(),
                                100,
                                1,
                                10
                            )
                        ).build()
                )
                .build()
        )

        val voidBiome = Biome.BiomeBuilder()
            .downfall(0f)
            .temperature(0.8f)
            .hasPrecipitation(false)
            .temperatureAdjustment(Biome.TemperatureModifier.NONE)
            .specialEffects(
                BiomeSpecialEffects.Builder()
                    .waterColor(0x3f76e4)
                    .waterFogColor(0x50533)
                    .fogColor(0)
                    .skyColor(0)
                    .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
                    .build()
            )
            .mobSpawnSettings(MobSpawnSettings.EMPTY)
            .generationSettings(BiomeGenerationSettings.EMPTY)

        entries.register(ModBiomes.PUBLIC_BLACK_VOID_KEY, voidBiome.build())
        entries.register(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY, voidBiome.build())

        entries.register(
            ModBiomes.PERSONAL_WHITE_VOID_KEY, Biome.BiomeBuilder()
                .downfall(0f)
                .temperature(0.8f)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .hasPrecipitation(false)
                .specialEffects(
                    BiomeSpecialEffects.Builder()
                        .waterColor(0x3f76e4)
                        .waterFogColor(0x50533)
                        .fogColor(0xffffff)
                        .skyColor(0xffffff)
                        .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
                        .backgroundMusic(
                            Music(
                                sound(entries, ModSoundEvents.WHITE_VOID),
                                0,
                                0,
                                true
                            )
                        ).build()
                )
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        )
    }

    fun bootstrapCarvers(entries: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        entries.register(
            ModCarvers.LIMBO, ConfiguredWorldCarver(
                ModCarvers.LIMBO_CARVER, CaveCarverConfiguration(
                    0.2f,
                    UniformHeight.of(VerticalAnchor.aboveBottom(0), VerticalAnchor.belowTop(8)),
                    ConstantFloat.of(0.5f),
                    VerticalAnchor.aboveBottom(10),
                    CarverDebugSettings.DEFAULT,
                    blockSet(entries, ModBlocks.UNRAVELLED_FABRIC.value()),
                    ConstantFloat.of(1f),
                    ConstantFloat.of(1f),
                    ConstantFloat.of(-0.7f)
                )
            )
        )
    }

    fun bootstrapStructures(context: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        val biomes = context.registrylookup<Biome?>(Registries.BIOME)
        val pools = context.registrylookup<StructureTemplatePool?>(Registries.TEMPLATE_POOL)

        registerStructure(
            context,
            biomes,
            pools,
            ModStructures.ENCLOSED_GATEWAY,
            ModBiomeTags.ENCLOSED_GATEWAY,
            ModGatewayPools.ENCLOSED_GATEWAY
        )
        registerStructure(
            context,
            biomes,
            pools,
            ModStructures.ENCLOSED_ENDSTONE_GATEWAY,
            ModBiomeTags.ENCLOSED_ENDSTONE_GATEWAY,
            ModGatewayPools.ENCLOSED_ENDSTONE_GATEWAY
        )
        registerStructure(
            context,
            biomes,
            pools,
            ModStructures.ENCLOSED_MUD_GATEWAY,
            ModBiomeTags.ENCLOSED_MUD_GATEWAY,
            ModGatewayPools.ENCLOSED_MUD_GATEWAY
        )
        context.register<Structure?>(
            ModStructures.ENCLOSED_PRISMARINE_GATEWAY,
            JigsawStructure(
                Structure.StructureSettings(
                    biomes.getOrThrow(ModBiomeTags.ENCLOSED_PRISMARINE_GATEWAY),
                    mutableMapOf<MobCategory?, StructureSpawnOverride?>(),
                    GenerationStep.Decoration.SURFACE_STRUCTURES,
                    TerrainAdjustment.BEARD_THIN
                ),
                pools.getOrThrow(ModGatewayPools.ENCLOSED_PRISMARINE_GATEWAY),
                1,
                ConstantHeight.of(VerticalAnchor.absolute(0)),
                true,
                Heightmap.Types.OCEAN_FLOOR_WG
            )
        )
        registerStructure(
            context,
            biomes,
            pools,
            ModStructures.ENCLOSED_QUARTZ_GATEWAY,
            ModBiomeTags.ENCLOSED_QUARTZ_GATEWAY,
            ModGatewayPools.ENCLOSED_QUARTZ_GATEWAY
        )
        registerStructure(
            context,
            biomes,
            pools,
            ModStructures.ENCLOSED_RED_SANDSTONE_GATEWAY,
            ModBiomeTags.ENCLOSED_RED_SANDSTONE_GATEWAY,
            ModGatewayPools.ENCLOSED_RED_SANDSTONE_GATEWAY
        )
        registerStructure(
            context,
            biomes,
            pools,
            ModStructures.ENCLOSED_SANDSTONE_GATEWAY,
            ModBiomeTags.ENCLOSED_SANDSTONE_GATEWAY,
            ModGatewayPools.ENCLOSED_SANDSTONE_GATEWAY
        )

        //        register(LIMBO_GATEWAY, ModBiomeTags.LIMBO_GATEWAY, ModGatewayPools.LIMBO_GATEWAY);

//        register(TWO_PILLARS, ModBiomeTags.TWO_PILLARS, ModGatewayPools.TWO_PILLARS);
//        register(SANDSTONE_PILLARS, ModBiomeTags.SANDSTONE_PILLARS, ModGatewayPools.SANDSTONE_PILLARS);
//        register(RED_SANDSTONE_PILLARS, ModBiomeTags.RED_SANDSTONE_PILLARS, ModGatewayPools.RED_SANDSTONE_PILLARS);
//        register(ICE_PILLARS, ModBiomeTags.ICE_PILLARS, ModGatewayPools.ICE_PILLARS);
    }

    private fun registerStructure(
        context: DimDoorsDynamicRegistryProvider.RegistrationHelper,
        biomes: HolderLookup<Biome?>,
        pools: HolderLookup<StructureTemplatePool?>,
        structure: ResourceKey<Structure?>?,
        biome: TagKey<Biome?>?,
        pool: ResourceKey<StructureTemplatePool?>?
    ) {
        context.register<Structure?>(
            structure,
            JigsawStructure(
                Structure.StructureSettings(
                    biomes.getOrThrow(biome),
                    mutableMapOf<MobCategory?, StructureSpawnOverride?>(),
                    GenerationStep.Decoration.SURFACE_STRUCTURES,
                    TerrainAdjustment.BEARD_THIN
                ),
                pools.getOrThrow(pool),
                1,
                ConstantHeight.of(VerticalAnchor.absolute(0)),
                true,
                Heightmap.Types.WORLD_SURFACE_WG
            )
        )
    }

    fun bootstrapProcessorLists(context: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        context.register<StructureProcessorList?>(
            ModProcessorLists.DUNGEON,
            StructureProcessorList(listOf(DestinationDataModifier.of(DefaultDungeonDestinations.shallowerDungeonDestination)))
        )
    }

    @JvmStatic
    fun bootstrapGatewayPools(context: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        val empty = context.lookup<StructureTemplatePool?>(Pools.EMPTY)
        val processorLists = context.registrylookup<StructureProcessorList?>(Registries.PROCESSOR_LIST)

        val dungeon = processorLists.getOrThrow(ModProcessorLists.DUNGEON)
        context.register(ModGatewayPools.ENCLOSED_GATEWAY,
            StructureTemplatePool(
                empty,
                listOf<Pair<Function<StructureTemplatePool.Projection, out StructurePoolElement>, Int>>(
                    StructurePoolElement.single("dimdoors:gateways/enclosed", dungeon) pair 50
                ), StructureTemplatePool.Projection.RIGID
            )
        )
        context.register(ModGatewayPools.ENCLOSED_ENDSTONE_GATEWAY, StructureTemplatePool(empty,
                listOf(
                    StructurePoolElement.single("dimdoors:gateways/enclosed_endstone", dungeon) pair  50

                ),
                StructureTemplatePool.Projection.RIGID
            )
        )
        context.register<StructureTemplatePool?>(
            ModGatewayPools.ENCLOSED_MUD_GATEWAY,
            StructureTemplatePool(
                empty,
                listOf(
                    StructurePoolElement.single("dimdoors:gateways/enclosed_mud", dungeon) pair 50
                ),
                StructureTemplatePool.Projection.RIGID
            )
        )
        context.register<StructureTemplatePool?>(
            ModGatewayPools.ENCLOSED_PRISMARINE_GATEWAY,
            StructureTemplatePool(
                empty,
                listOf(
                    StructurePoolElement.single("dimdoors:gateways/enclosed_prismarine", dungeon) pair 50
                ),
                StructureTemplatePool.Projection.RIGID
            )
        )
        context.register<StructureTemplatePool?>(
            ModGatewayPools.ENCLOSED_QUARTZ_GATEWAY,
            StructureTemplatePool(
                empty,
                listOf(
                    StructurePoolElement.single("dimdoors:gateways/enclosed_quartz", dungeon) pair 50
                ),
                StructureTemplatePool.Projection.RIGID
            )
        )
        context.register<StructureTemplatePool?>(
            ModGatewayPools.ENCLOSED_RED_SANDSTONE_GATEWAY,
            StructureTemplatePool(
                empty,
                listOf(
                    StructurePoolElement.single("dimdoors:gateways/enclosed_red_sandstone", dungeon) pair 50
                ),
                StructureTemplatePool.Projection.RIGID
            )
        )
        context.register<StructureTemplatePool?>(
            ModGatewayPools.ENCLOSED_SANDSTONE_GATEWAY,
            StructureTemplatePool(
                empty,
                listOf(
                    StructurePoolElement.single("dimdoors:gateways/enclosed_sandstone", dungeon) pair 50
                ),
                StructureTemplatePool.Projection.RIGID
            )
        )

        //        context.register(LIMBO_GATEWAY, new StructureTemplatePool(empty, ImmutablelistOf(Pair.of(StructurePoolElement.single("dimdoors:gateways/limbo", dungeon), 50)), StructureTemplatePool.Projection.RIGID));

//        context.register(TWO_PILLARS, new StructureTemplatePool(empty, ImmutablelistOf(Pair.of(StructurePoolElement.single("dimdoors:gateways/two_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(SANDSTONE_PILLARS, new StructureTemplatePool(empty, ImmutablelistOf(Pair.of(StructurePoolElement.single("dimdoors:gateways/sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(RED_SANDSTONE_PILLARS, new StructureTemplatePool(empty, ImmutablelistOf(Pair.of(StructurePoolElement.single("dimdoors:gateways/red_sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(ICE_PILLARS, new StructureTemplatePool(empty, ImmutablelistOf(Pair.of(StructurePoolElement.single("dimdoors:gateways/red_sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
    }

    @JvmStatic
    fun bootstrapStructureSets(context: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        context.register<StructureSet?>(
            ModStructureSets.GATEWAYS, StructureSet(
                listOf<StructureSet.StructureSelectionEntry?>(
                    StructureSet.StructureSelectionEntry(
                        context.lookup<Structure?>(ModStructures.ENCLOSED_GATEWAY),
                        1
                    ),  //                        new StructureSet.StructureSelectionEntry(context.lookup(ModStructures.ENCLOSED_ENDSTONE_GATEWAY), 1),
                    StructureSet.StructureSelectionEntry(
                        context.lookup<Structure?>(ModStructures.ENCLOSED_MUD_GATEWAY),
                        1
                    ),
                    StructureSet.StructureSelectionEntry(
                        context.lookup<Structure?>(ModStructures.ENCLOSED_PRISMARINE_GATEWAY),
                        1
                    ),
                    StructureSet.StructureSelectionEntry(
                        context.lookup<Structure?>(ModStructures.ENCLOSED_QUARTZ_GATEWAY),
                        1
                    ),
                    StructureSet.StructureSelectionEntry(
                        context.lookup<Structure?>(ModStructures.ENCLOSED_RED_SANDSTONE_GATEWAY),
                        1
                    ),
                    StructureSet.StructureSelectionEntry(
                        context.lookup<Structure?>(ModStructures.ENCLOSED_SANDSTONE_GATEWAY),
                        1
                    ) /*,
                        new StructureSet.StructureSelectionEntry(context.lookup(ModStructures.LIMBO_GATEWAY), 1)*/
                ),
                RandomSpreadStructurePlacement(
                    15,
                    5,
                    RandomSpreadType.TRIANGULAR,
                    23165478
                )
            )
        )
    }

    @JvmStatic
    fun bootstrapJukeboxSongs(context: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        context.register<JukeboxSong?>(
            ModJukeboxSongs.CREEPY,
            JukeboxSong(
                sound(context, ModSoundEvents.CREEPY),
                Component.translatable("item.dimdoors.creepy_record.desc"),
                317f,
                10
            )
        )
        context.register<JukeboxSong?>(
            ModJukeboxSongs.WHITE_VOID,
            JukeboxSong(
                sound(context, ModSoundEvents.WHITE_VOID),
                Component.translatable("item.dimdoors.white_void_record.desc"),
                225f,
                10
            )
        )
        context.register<JukeboxSong?>(
            ModJukeboxSongs.THEY_STARE_BACK,
            JukeboxSong(
                sound(context, ModSoundEvents.THEY_STARE_BACK),
                Component.translatable("item.dimdoors.they_stare_back_record.desc"),
                226f,
                10
            )
        )
    }

    private fun sound(
        context: DimDoorsDynamicRegistryProvider.RegistrationHelper,
        soundEvent: SoundEvent
    ): Holder<SoundEvent?> {
        return context.lookup<SoundEvent?>(
            ResourceKey.create<SoundEvent?>(
                Registries.SOUND_EVENT,
                soundEvent.location
            )
        )
    }

    private fun blockSet(
        context: DimDoorsDynamicRegistryProvider.RegistrationHelper, vararg blocks: Block
    ): HolderSet<Block> {
        val blockLookup = context.registrylookup(Registries.BLOCK)
        return HolderSet.direct(
            Stream.of(*blocks).map { block: Block -> blockLookup.getOrThrow(block.builtInRegistryHolder().key()
                )
            }.toList()
        )
    }

    @JvmStatic
    fun bootstrapEnchants(context: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        val enchantments: HolderGetter<Enchantment?> = context.registrylookup(Registries.ENCHANTMENT)
        val items: HolderGetter<Item?> = context.registrylookup(Registries.ITEM)

        context.register(
            ModEnchants.STRING_THEORY_ENCHANTMENT, Enchantment.enchantment(
                Enchantment.definition(
                    items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE), 10, 4,
                    Enchantment.dynamicCost(1, 11),
                    Enchantment.dynamicCost(12, 11), 1,
                    EquipmentSlotGroup.ARMOR
                )
            )
                .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                .build(ModEnchants.STRING_THEORY_ENCHANTMENT.location())
        )

        context.register(
            ModEnchants.TREPIDATION_ENCHANTMENT, Enchantment.enchantment(
                Enchantment.definition(
                    items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE), 2, 3,
                    Enchantment.dynamicCost(5, 8),
                    Enchantment.dynamicCost(25, 8), 4,
                    EquipmentSlotGroup.CHEST
                )
            )
                .withEffect(
                    EnchantmentEffectComponents.TICK,
                    PlaySoundEffect(
                        Holder.direct(SoundEvents.WARDEN_HEARTBEAT),
                        ConstantFloat.of(0.8f),
                        ConstantFloat.of(1.0f)
                    ),
                    EntityNearBy.nearby(
                        EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(5.0f, 5.0f)),
                        EntityPredicate.Builder.entity()
                            .entityType(
                                EntityTypePredicate(
                                    context.registrylookup<EntityType<*>?>(Registries.ENTITY_TYPE)
                                        .getOrThrow(ModEntityTypeTags.TREPIDATION_DETECTED)
                                )
                            )
                            .build(),
                        40
                    )
                ).build(ModEnchants.TREPIDATION_ENCHANTMENT.location())
        )

        context.register<Enchantment?>(
            ModEnchants.TRANSCENDENT_ENCHANTMENT, Enchantment.enchantment(
                Enchantment.definition(
                    items.getOrThrow(ModItemTags.TRANSCENDENT_ENCHANTABLE), 2, 1,
                    Enchantment.dynamicCost(15, 10),
                    Enchantment.dynamicCost(45, 10), 4,
                    EquipmentSlotGroup.MAINHAND
                )
            )
                .withEffect<EnchantmentEntityEffect?>(
                    EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                    TranscendentProjectileEffect.INSTANCE
                ).build(ModEnchants.TRANSCENDENT_ENCHANTMENT.location())
        )

        context.register<Enchantment?>(
            ModEnchants.RENDING_ENCHANTMENT, Enchantment.enchantment(
                Enchantment.definition(
                    items.getOrThrow(ItemTags.MINING_ENCHANTABLE), 2, 2,
                    Enchantment.dynamicCost(15, 9),
                    Enchantment.dynamicCost(45, 9), 4,
                    EquipmentSlotGroup.MAINHAND
                )
            ).build(ModEnchants.RENDING_ENCHANTMENT.location())
        )
    }


    @JvmStatic
    fun bootstrapPaintings(context: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        registerPainting(context, ModPaintings.LIMBO, 4, 2)
        registerPainting(context, ModPaintings.PORTAL, 2, 4)
        registerPainting(context, ModPaintings.FREEDOM, 2, 2)
        registerPainting(context, ModPaintings.EYES, 2, 2)
        registerPainting(context, ModPaintings.GATEWAY_AT_NIGHT, 4, 2)
    }

    private fun registerPainting(
        bootstrapContext: DimDoorsDynamicRegistryProvider.RegistrationHelper,
        resourceKey: ResourceKey<PaintingVariant>,
        width: Int,
        height: Int
    ) {
        bootstrapContext.register(resourceKey, PaintingVariant(width, height, resourceKey.location()))
    }

    private infix fun <T: Any, V: Any> T.pair(other: V) = Pair.of(this, other)
}
