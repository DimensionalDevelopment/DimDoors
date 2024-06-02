package org.dimdev.dimdoors.forge.world.feature.gateway;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LimboGatewayFeature extends Feature<NoneFeatureConfiguration> {
    public LimboGatewayFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		LimboGateway.INSTANCE.generate(context.level(), context.origin());
		return true;

	}
}
