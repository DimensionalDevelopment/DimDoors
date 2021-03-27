package org.dimdev.dimdoors.world.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.mixin.accessor.GenerationSettingsAccessor;
import org.dimdev.dimdoors.world.feature.decorator.EternalFluidLakeDecorator;
import org.dimdev.dimdoors.world.feature.gateway.LimboGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SandstonePillarsV2Gateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicV2Gateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicV2GatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicV2GatewayFeatureConfig;
import org.dimdev.dimdoors.world.feature.gateway.schematic.TwoPillarsV2Gateway;

import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

import net.fabricmc.loader.api.FabricLoader;

// IMPORTANT NOTE
// Do not remove deprecated stuff
@SuppressWarnings("DeprecatedIsStillUsed")
public final class ModFeatures {
    public static final Feature<SchematicV2GatewayFeatureConfig> SCHEMATIC_GATEWAY_FEATURE = new SchematicV2GatewayFeature(SchematicV2GatewayFeatureConfig.CODEC);
    public static final Feature<DefaultFeatureConfig> LIMBO_GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "limbo_gateway"), new LimboGatewayFeature());
    public static final SchematicV2Gateway SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsV2Gateway();
    public static final SchematicV2Gateway TWO_PILLARS_GATEWAY = new TwoPillarsV2Gateway();
    @Deprecated public static final Decorator<ChanceDecoratorConfig> ETERNAL_FLUID_LAKE_DECORATOR = new EternalFluidLakeDecorator(ChanceDecoratorConfig.CODEC);
    public static final ConfiguredFeature<?, ?> SANDSTONE_PILLARS_FEATURE;
    public static final ConfiguredFeature<?, ?> TWO_PILLARS_FEATURE;
    public static final ConfiguredFeature<?, ?> LIMBO_GATEWAY_CONFIGURED_FEATURE;
    public static final ConfiguredFeature<?, ?> SOLID_STATIC_ORE;
    @Deprecated public static final ConfiguredFeature<?, ?> ETERNAL_FLUID_LAKE = Feature.LAKE.configure(new SingleStateFeatureConfig(ModBlocks.ETERNAL_FLUID.getDefaultState())).decorate(ETERNAL_FLUID_LAKE_DECORATOR.configure(new ChanceDecoratorConfig(20)));

    public static void init() {
        SANDSTONE_PILLARS_GATEWAY.init();
        TWO_PILLARS_GATEWAY.init();
        Registry.register(Registry.FEATURE, new Identifier("dimdoors", "schematic_gateway"), SCHEMATIC_GATEWAY_FEATURE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "sandstone_pillars"), SANDSTONE_PILLARS_FEATURE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "limbo_gateway"), LIMBO_GATEWAY_CONFIGURED_FEATURE);
        Registry.register(Registry.DECORATOR, new Identifier("dimdoors", "eternal_fluid_lake"), ETERNAL_FLUID_LAKE_DECORATOR);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "eternal_fluid_lake"), ETERNAL_FLUID_LAKE);
        synchronized (BuiltinRegistries.BIOME) {
            BuiltinRegistries.BIOME.stream()
                    .filter(biome ->
                            biome.getCategory() != Biome.Category.NONE &&
                                    biome.getCategory() != Biome.Category.THEEND &&
                                    biome.getCategory() != Biome.Category.NETHER &&
                                    biome.getCategory() != Biome.Category.OCEAN &&
                                    biome.getCategory() != Biome.Category.MUSHROOM
                    )
                    .forEach(biome -> {
                        int index = GenerationStep.Feature.SURFACE_STRUCTURES.ordinal();
                        List<List<Supplier<ConfiguredFeature<?, ?>>>> features = new ArrayList<>(biome.getGenerationSettings().getFeatures());
                        List<Supplier<ConfiguredFeature<?, ?>>> surfaceStructures = new ArrayList<>(features.get(index));
                        surfaceStructures.add(() -> TWO_PILLARS_FEATURE);
                        features.set(index, surfaceStructures);
                        ((GenerationSettingsAccessor) biome.getGenerationSettings()).setFeatures(features);
                    });
        }
    }

    static {
        int gatewayChance = FabricLoader.getInstance().isDevelopmentEnvironment() ? 20 : DimensionalDoorsInitializer.getConfig().getWorldConfig().gatewayGenChance;
        SANDSTONE_PILLARS_FEATURE = SCHEMATIC_GATEWAY_FEATURE.configure(new SchematicV2GatewayFeatureConfig(SchematicV2Gateway.ID_SCHEMATIC_MAP.inverse().get(SANDSTONE_PILLARS_GATEWAY))).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP.applyChance(gatewayChance));
        TWO_PILLARS_FEATURE = SCHEMATIC_GATEWAY_FEATURE.configure(new SchematicV2GatewayFeatureConfig(SchematicV2Gateway.ID_SCHEMATIC_MAP.inverse().get(TWO_PILLARS_GATEWAY))).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP.applyChance(gatewayChance));
        LIMBO_GATEWAY_CONFIGURED_FEATURE = LIMBO_GATEWAY_FEATURE.configure(DefaultFeatureConfig.INSTANCE).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP.applyChance(gatewayChance));
		SOLID_STATIC_ORE = Feature.ORE.configure(new OreFeatureConfig(new BlockMatchRuleTest(ModBlocks.UNRAVELLED_FABRIC), ModBlocks.SOLID_STATIC.getDefaultState(), 4)).range(new RangeDecoratorConfig(YOffset.getBottom(), YOffset.getTop())).repeat(3);
    }
}
