package org.dimdev.dimdoors.world.feature;

import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.world.feature.gateway.GatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.GatewayFeatureConfig;
import org.dimdev.dimdoors.world.feature.gateway.SandstonePillarsGateway;
import org.dimdev.dimdoors.world.feature.gateway.SchematicGateway;
import org.dimdev.dimdoors.world.feature.gateway.TwoPillarsGateway;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public final class ModFeatures {
    public static final Feature<GatewayFeatureConfig> GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "gateway"), new GatewayFeature(GatewayFeatureConfig.CODEC));
    public static final SchematicGateway SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsGateway();
    public static final SchematicGateway TWO_PILLARS_GATEWAY = new TwoPillarsGateway();
    public static final ConfiguredFeature<?, ?> SANDSTONE_PILLARS_FEATURE = GATEWAY_FEATURE.configure(GatewayFeatureConfig.SANDSTONE_PILLARS_CONFIG).decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(ModConfig.WORLD.gatewayGenChance + 20)));
    public static final ConfiguredFeature<?, ?> TWO_PILLARS_FEATURE = GATEWAY_FEATURE.configure(GatewayFeatureConfig.TWO_PILLARS_CONFIG).decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(ModConfig.WORLD.gatewayGenChance + 20)));

    public static void init() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "sandstone_pillars"), SANDSTONE_PILLARS_FEATURE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "two_pillars"), TWO_PILLARS_FEATURE);
    }
}
