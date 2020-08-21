package org.dimdev.dimdoors.world.feature.gateway;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class GatewayFeature extends Feature<GatewayFeatureConfig> {
    public GatewayFeature(Codec<GatewayFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, GatewayFeatureConfig config) {
        config.gateway.generate(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return true;
    }
}
