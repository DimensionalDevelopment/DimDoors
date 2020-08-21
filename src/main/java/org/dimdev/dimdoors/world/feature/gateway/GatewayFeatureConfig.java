package org.dimdev.dimdoors.world.feature.gateway;

import net.minecraft.world.gen.feature.FeatureConfig;

public class GatewayFeatureConfig implements FeatureConfig {
    public final SchematicGateway gateway;

    public GatewayFeatureConfig(SchematicGateway gateway) {
        this.gateway = gateway;
    }
}
