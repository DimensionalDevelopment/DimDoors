package org.dimdev.dimdoors.world.feature.gateway.schematic;

import com.mojang.serialization.Codec;

import net.minecraft.block.AirBlock;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SchematicV2GatewayFeature extends Feature<SchematicV2GatewayFeatureConfig> {
    public SchematicV2GatewayFeature(Codec<SchematicV2GatewayFeatureConfig> codec) {
        super(codec);
    }

	@Override
    public boolean generate(FeatureContext<SchematicV2GatewayFeatureConfig> featureContext) {
        if (featureContext.getWorld().getBlockState(featureContext.getOrigin()).getBlock() instanceof AirBlock && featureContext.getConfig().getGateway().test(featureContext.getWorld(), featureContext.getOrigin())) {
			featureContext.getConfig().getGateway().generate(featureContext.getWorld(), featureContext.getOrigin());
            return true;
        }
        return false;
    }
}
