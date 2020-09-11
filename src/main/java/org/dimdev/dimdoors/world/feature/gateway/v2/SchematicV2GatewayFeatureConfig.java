package org.dimdev.dimdoors.world.feature.gateway.v2;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.feature.FeatureConfig;

public class SchematicV2GatewayFeatureConfig implements FeatureConfig {
    public static final Codec<SchematicV2GatewayFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Codec.STRING.fieldOf("gatewayId").forGetter(SchematicV2GatewayFeatureConfig::getGatewayId)
        ).apply(instance, SchematicV2GatewayFeatureConfig::new);
    });

    private final SchematicV2Gateway gateway;
    private final String gatewayId;

    public SchematicV2GatewayFeatureConfig(String id) {
        this.gatewayId = id;
        this.gateway = SchematicV2Gateway.ID_SCHEMATIC_MAP.get(id);
    }

    public SchematicV2Gateway getGateway() {
        return this.gateway;
    }

    public String getGatewayId() {
        return this.gatewayId;
    }
}
