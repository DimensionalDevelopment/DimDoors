package org.dimdev.dimdoors.world.feature;

import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.feature.gateway.LimboGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SandstonePillarsV2Gateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicV2Gateway;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicV2GatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.schematic.SchematicV2GatewayFeatureConfig;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

import net.fabricmc.loader.api.FabricLoader;

public final class ModFeatures {
    public static final Feature<SchematicV2GatewayFeatureConfig> SCHEMATIC_GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "schematic_gateway"), new SchematicV2GatewayFeature(SchematicV2GatewayFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> LIMBO_GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "limbo_gateway"), new LimboGatewayFeature());
    public static final SchematicV2Gateway SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsV2Gateway();
    public static final ConfiguredFeature<?, ?> SANDSTONE_PILLARS_FEATURE;
    public static final ConfiguredFeature<?, ?> LIMBO_GATEWAY_CONFIGURED_FEATURE;
    public static ConfiguredFeature<?, ?> ETERNAL_FLUID_LAKE;

    public static void init() {
        SANDSTONE_PILLARS_GATEWAY.init();
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "sandstone_pillars"), SANDSTONE_PILLARS_FEATURE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "limbo_gateway"), LIMBO_GATEWAY_CONFIGURED_FEATURE);
        ETERNAL_FLUID_LAKE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "eternal_fluid_lake"), Feature.LAKE.configure(new SingleStateFeatureConfig(ModBlocks.ETERNAL_FLUID.getDefaultState())).decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(5, 70, 128)).applyChance(20)));
    }

    static {
        int gatewayChance = FabricLoader.getInstance().isDevelopmentEnvironment() ? 20 : ModConfig.INSTANCE.getWorldConfig().gatewayGenChance;
        SANDSTONE_PILLARS_FEATURE = SCHEMATIC_GATEWAY_FEATURE.configure(new SchematicV2GatewayFeatureConfig(SchematicV2Gateway.SCHEMATIC_ID_MAP.get(SANDSTONE_PILLARS_GATEWAY)))
                .decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP
                        .applyChance(gatewayChance));
        LIMBO_GATEWAY_CONFIGURED_FEATURE = LIMBO_GATEWAY_FEATURE.configure(DefaultFeatureConfig.INSTANCE).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP.applyChance(gatewayChance));
    }
}
