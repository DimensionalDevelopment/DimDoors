package org.dimdev.dimdoors.world.feature;

import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.feature.gateway.SandstonePillarsGateway;
import org.dimdev.dimdoors.world.feature.gateway.SchematicGateway;
import org.dimdev.dimdoors.world.feature.gateway.SchematicGatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.SchematicGatewayFeatureConfig;
import org.dimdev.dimdoors.world.feature.gateway.TwoPillarsGateway;
import org.dimdev.dimdoors.world.feature.gateway.v2.SandstonePillarsV2Gateway;
import org.dimdev.dimdoors.world.feature.gateway.v2.SchematicV2Gateway;
import org.dimdev.dimdoors.world.feature.gateway.v2.SchematicV2GatewayFeature;
import org.dimdev.dimdoors.world.feature.gateway.v2.SchematicV2GatewayFeatureConfig;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;

import net.fabricmc.loader.api.FabricLoader;

public final class ModFeatures {
    public static final Feature<SchematicV2GatewayFeatureConfig> GATEWAY_FEATURE_V2 = Registry.register(Registry.FEATURE, new Identifier("dimdoors", "gateway_v2"), new SchematicV2GatewayFeature(SchematicV2GatewayFeatureConfig.CODEC));
    public static final SchematicV2Gateway SANDSTONE_PILLARS_GATEWAY_V2;
    public static final ConfiguredFeature<?, ?> SANDSTONE_PILLARS_FEATURE_V2;

    public static void init() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("dimdoors", "sandstone_pillars_v2"), SANDSTONE_PILLARS_FEATURE_V2);
    }

    static {
        ModBlocks.init();
        SANDSTONE_PILLARS_GATEWAY_V2 = new SandstonePillarsV2Gateway();

        int gatewayChance = FabricLoader.getInstance().isDevelopmentEnvironment() ? 20 : ModConfig.INSTANCE.getWorldConfig().gatewayGenChance;
        SANDSTONE_PILLARS_FEATURE_V2 = GATEWAY_FEATURE_V2.configure(new SchematicV2GatewayFeatureConfig(SchematicV2Gateway.SCHEMATIC_ID_MAP.get(SANDSTONE_PILLARS_GATEWAY_V2)))
                .decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP
                        .applyChance(gatewayChance));
    }
}
