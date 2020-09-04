package org.dimdev.dimdoors.world.feature;

import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.world.feature.gateway.SchematicGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.SchematicGatewayFeatureConfig;
import org.dimdev.dimdoors.world.feature.gateway.SandstonePillarsGateway;
import org.dimdev.dimdoors.world.feature.gateway.SchematicGateway;
import org.dimdev.dimdoors.world.feature.gateway.TwoPillarsGateway;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;

public final class ModFeatures {
    public static final Feature<SchematicGatewayFeatureConfig> GATEWAY_FEATURE = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "gateway"), new SchematicGatewayFeature(SchematicGatewayFeatureConfig.CODEC));
    public static final SchematicGateway SANDSTONE_PILLARS_GATEWAY = new SandstonePillarsGateway();
    public static final SchematicGateway TWO_PILLARS_GATEWAY = new TwoPillarsGateway();
    public static final ConfiguredFeature<?, ?> SANDSTONE_PILLARS_FEATURE;
    public static final ConfiguredFeature<?, ?> TWO_PILLARS_FEATURE;

    public static void init() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "sandstone_pillars"), SANDSTONE_PILLARS_FEATURE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "two_pillars"), TWO_PILLARS_FEATURE);
    }

    static {
        SANDSTONE_PILLARS_FEATURE = GATEWAY_FEATURE.configure(new SchematicGatewayFeatureConfig(SchematicGateway.SCHEMATIC_ID_MAP.get(SANDSTONE_PILLARS_GATEWAY)))
                .decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP
                        .applyChance(ModConfig.WORLD.gatewayGenChance));
        TWO_PILLARS_FEATURE = GATEWAY_FEATURE.configure(new SchematicGatewayFeatureConfig(SchematicGateway.SCHEMATIC_ID_MAP.get(TWO_PILLARS_GATEWAY)))
                .decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP
                        .applyChance(ModConfig.WORLD.gatewayGenChance));
    }
}
