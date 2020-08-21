package org.dimdev.dimdoors.world.feature.gateway;

import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.world.feature.ModFeatures;

import net.minecraft.world.gen.feature.FeatureConfig;

public class GatewayFeatureConfig implements FeatureConfig {
    public static final GatewayFeatureConfig SANDSTONE_PILLARS_CONFIG = new GatewayFeatureConfig(ModFeatures.SANDSTONE_PILLARS_GATEWAY);
    public static final GatewayFeatureConfig TWO_PILLARS_CONFIG = new GatewayFeatureConfig(ModFeatures.TWO_PILLARS_GATEWAY);
    public static final Codec<GatewayFeatureConfig> CODEC = Codec.unit(SANDSTONE_PILLARS_CONFIG);
    public final SchematicGateway gateway;

    private GatewayFeatureConfig(SchematicGateway gateway) {
        this.gateway = gateway;
    }
}
