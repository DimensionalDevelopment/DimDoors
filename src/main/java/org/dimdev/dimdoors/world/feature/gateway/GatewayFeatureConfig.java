package org.dimdev.dimdoors.world.feature.gateway;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.feature.FeatureConfig;

public class GatewayFeatureConfig implements FeatureConfig {
    public static final Codec<GatewayFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> {
       return instance.group(Codec.STRING.fieldOf("id").forGetter((config) -> {
           return config.gatewayId;
       })).apply(instance, GatewayFeatureConfig::new);
    });
    private final SchematicGateway gateway;
    private final String gatewayId;

    public SchematicGateway getGateway() {
        return gateway;
    }

    public GatewayFeatureConfig(String gatewayId) {
        this.gatewayId = gatewayId;
        this.gateway = SchematicGateway.ID_SCHEMATIC_MAP.get(gatewayId);
    }
}
