package org.dimdev.dimdoors.world.feature.gateway.v2;

import java.util.Random;

import org.dimdev.dimdoors.world.feature.gateway.SchematicGatewayFeatureConfig;
import com.mojang.serialization.Codec;

import net.minecraft.block.AirBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class SchematicV2GatewayFeature extends Feature<SchematicV2GatewayFeatureConfig> {
    public SchematicV2GatewayFeature(Codec<SchematicV2GatewayFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SchematicV2GatewayFeatureConfig featureConfig) {
        if (world.getBlockState(blockPos).getBlock() instanceof AirBlock && world.getBlockState(blockPos.down()).getBlock() instanceof FallingBlock) {
            featureConfig.getGateway().generate(world, blockPos);
            return true;
        }
        return false;
    }
}
