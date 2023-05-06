package org.dimdev.dimdoors.world.feature.gateway;

import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class LimboGatewayFeature extends Feature<DefaultFeatureConfig> {
    public LimboGatewayFeature() {
        super(DefaultFeatureConfig.CODEC);
    }

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		LimboGateway.INSTANCE.generate(context.getWorld(), context.getOrigin());
		return true;
	}
}
