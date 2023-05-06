package org.dimdev.dimdoors.world.feature.gateway.schematic;

import com.mojang.serialization.Codec;

import net.minecraft.block.AirBlock;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SchematicGatewayFeature extends Feature<SchematicGatewayFeatureConfig> {
    public SchematicGatewayFeature(Codec<SchematicGatewayFeatureConfig> codec) {
        super(codec);
    }

	@Override
    public boolean generate(FeatureContext<SchematicGatewayFeatureConfig> featureContext) {
        if (featureContext.getWorld().getBlockState(featureContext.getOrigin()).getBlock() instanceof AirBlock && featureContext.getConfig().getGateway().test(featureContext.getWorld(), featureContext.getOrigin())) {
			featureContext.getConfig().getGateway().generate(featureContext.getWorld(), featureContext.getOrigin());
            return true;
        }
        return false;
    }
}
