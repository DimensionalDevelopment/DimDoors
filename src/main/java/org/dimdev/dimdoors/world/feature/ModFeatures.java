package org.dimdev.dimdoors.world.feature;

import com.google.common.collect.ImmutableList;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.feature.gateway.LimboGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SandstonePillarsGateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicGatewayFeatureConfig;
import org.dimdev.dimdoors.world.feature.gateway.schematic.TwoPillarsGateway;

import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.HeightmapPlacementModifier;
import net.minecraft.world.gen.decorator.RarityFilterPlacementModifier;
import net.minecraft.world.gen.decorator.SquarePlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;

// IMPORTANT NOTE
// Do not remove deprecated stuff
@SuppressWarnings("DeprecatedIsStillUsed")
public final class ModFeatures {
    public static final Feature<SchematicGatewayFeatureConfig> SCHEMATIC_GATEWAY_FEATURE = new SchematicGatewayFeature(SchematicGatewayFeatureConfig.CODEC);
    public static final Feature<DefaultFeatureConfig> LIMBO_GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "limbo_gateway"), new LimboGatewayFeature());
    public static final SchematicGateway SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsGateway();
    public static final SchematicGateway TWO_PILLARS_GATEWAY = new TwoPillarsGateway();
//    @Deprecated public static final Decorator<ChanceDecoratorConfig> ETERNAL_FLUID_LAKE_DECORATOR = new EternalFluidLakeDecorator(ChanceDecoratorConfig.CODEC);
    public static final PlacedFeature SANDSTONE_PILLARS_FEATURE;
    public static final PlacedFeature TWO_PILLARS_FEATURE;
//    public static final ConfiguredFeature<?, ?> LIMBO_GATEWAY_CONFIGURED_FEATURE;
//    public static final ConfiguredFeature<?, ?> SOLID_STATIC_ORE;
//    public static final ConfiguredFeature<?, ?> DECAYED_BLOCK_ORE;
    public static final RegistryKey<PlacedFeature> SANDSTONE_PILLARS_KEY = RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier("dimdoors", "sandstone_pillars"));
    public static final RegistryKey<PlacedFeature> TWO_PILLARS_KEY = RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier("dimdoors", "two_pillars"));

//    @Deprecated public static final ConfiguredFeature<?, ?> ETERNAL_FLUID_LAKE = Feature.LAKE.configure(new SingleStateFeatureConfig(ModBlocks.ETERNAL_FLUID.getDefaultState())).decorate(ETERNAL_FLUID_LAKE_DECORATOR.configure(new ChanceDecoratorConfig(20)));

    public static void init() {
        SANDSTONE_PILLARS_GATEWAY.init();
        TWO_PILLARS_GATEWAY.init();
        Registry.register(Registry.FEATURE, new Identifier("dimdoors", "schematic_gateway"), SCHEMATIC_GATEWAY_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, SANDSTONE_PILLARS_KEY.getValue(), SANDSTONE_PILLARS_FEATURE);
		Registry.register(BuiltinRegistries.PLACED_FEATURE, TWO_PILLARS_KEY.getValue(), TWO_PILLARS_FEATURE);
//		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "solid_static_ore"), SOLID_STATIC_ORE);
//		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "decayed_block_ore"), DECAYED_BLOCK_ORE);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "limbo_gateway"), LIMBO_GATEWAY_CONFIGURED_FEATURE);
//        Registry.register(Registry.DECORATOR, new Identifier("dimdoors", "eternal_fluid_lake"), ETERNAL_FLUID_LAKE_DECORATOR);
//        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "eternal_fluid_lake"), ETERNAL_FLUID_LAKE);

		BiomeModifications.addFeature(ctx -> {
			Biome biome = ctx.getBiome();
			return biome.getCategory() != Category.NONE &&
					biome.getCategory() != Category.THEEND &&
					biome.getCategory() != Category.NETHER &&
					biome.getCategory() != Category.OCEAN &&
					biome.getCategory() != Category.MUSHROOM &&
					biome.getCategory() != Category.DESERT;
		}, GenerationStep.Feature.SURFACE_STRUCTURES, TWO_PILLARS_KEY);
		BiomeModifications.addFeature(ctx -> ctx.getBiome().getCategory() == Category.DESERT, GenerationStep.Feature.SURFACE_STRUCTURES, SANDSTONE_PILLARS_KEY);
    }

    static {
        int gatewayChance = /*FabricLoader.getInstance().isDevelopmentEnvironment() ? 20 : */DimensionalDoorsInitializer.getConfig().getWorldConfig().gatewayGenChance;
        SANDSTONE_PILLARS_FEATURE = SCHEMATIC_GATEWAY_FEATURE.configure(new SchematicGatewayFeatureConfig(SchematicGateway.ID_SCHEMATIC_MAP.inverse().get(SANDSTONE_PILLARS_GATEWAY))).withPlacement(ImmutableList.of(SquarePlacementModifier.of(), HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG), RarityFilterPlacementModifier.of(gatewayChance))); //.decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP.applyChance(gatewayChance));
        TWO_PILLARS_FEATURE = SCHEMATIC_GATEWAY_FEATURE.configure(new SchematicGatewayFeatureConfig(SchematicGateway.ID_SCHEMATIC_MAP.inverse().get(TWO_PILLARS_GATEWAY))).withPlacement(ImmutableList.of(SquarePlacementModifier.of(), HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG), RarityFilterPlacementModifier.of(gatewayChance))); //.decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP.applyChance(gatewayChance));
//        LIMBO_GATEWAY_CONFIGURED_FEATURE = LIMBO_GATEWAY_FEATURE.configure(DefaultFeatureConfig.INSTANCE).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP.applyChance(gatewayChance));
//		SOLID_STATIC_ORE = Feature.ORE.configure(new OreFeatureConfig(new BlockMatchRuleTest(ModBlocks.UNRAVELLED_FABRIC), ModBlocks.SOLID_STATIC.getDefaultState(), 4)).uniformRange(YOffset.getBottom(), YOffset.getTop()).repeat(3);
//        DECAYED_BLOCK_ORE = Feature.ORE.configure(new OreFeatureConfig(new BlockMatchRuleTest(ModBlocks.UNRAVELLED_FABRIC), ModBlocks.DECAYED_BLOCK.getDefaultState(), 64)).uniformRange(YOffset.fixed(0), YOffset.fixed(79)).repeat(2);
    }
}
