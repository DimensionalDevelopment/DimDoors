package org.dimdev.dimdoors.world.feature.gateway;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.AirBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class SchematicGatewayFeature extends Feature<SchematicGatewayFeatureConfig> {
    public SchematicGatewayFeature(Codec<SchematicGatewayFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SchematicGatewayFeatureConfig config) {
        if (world.getBlockState(blockPos).getBlock() instanceof AirBlock && world.getBlockState(blockPos.down()).getBlock() instanceof FallingBlock) {
            config.getGateway().generate(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            return true;
        }
        return false;
    }
}
