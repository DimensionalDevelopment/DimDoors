package org.dimdev.dimdoors.world.feature.gateway.schematic;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class SchematicGatewayFeature extends Feature<SchematicGatewayFeatureConfig> {
    public SchematicGatewayFeature(Codec<SchematicGatewayFeatureConfig> codec) {
        super(codec);
    }

	@Override
    public boolean place(FeaturePlaceContext<SchematicGatewayFeatureConfig> featureContext) {
        if (featureContext.level().getBlockState(featureContext.origin()).getBlock() instanceof AirBlock && featureContext.config().getGateway().test(featureContext.level(), featureContext.origin())) {
			featureContext.config().getGateway().generate(featureContext.level(), featureContext.origin());
            return true;
        }
        return false;
    }
}
