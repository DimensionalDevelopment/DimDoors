package org.dimdev.dimdoors.world.feature.gateway.schematic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.feature.FeatureConfig;

public class SchematicGatewayFeatureConfig implements FeatureConfig {
    public static final Codec<SchematicGatewayFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			Codec.STRING.fieldOf("gatewayId").forGetter(SchematicGatewayFeatureConfig::getGatewayId)
	).apply(instance, SchematicGatewayFeatureConfig::new));

    private final SchematicGateway gateway;
    private final String gatewayId;

    public SchematicGatewayFeatureConfig(String id) {
        this.gatewayId = id;
        this.gateway = SchematicGateway.ID_SCHEMATIC_MAP.get(id);
    }

    public SchematicGateway getGateway() {
        return this.gateway;
    }

    public String getGatewayId() {
        return this.gatewayId;
    }
}
