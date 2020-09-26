package org.dimdev.dimdoors.world.feature.gateway;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;

public enum LimboGateway implements Gateway {
    INSTANCE;

    @Override
    public void generate(StructureWorldAccess world, BlockPos pos) {
        BlockState unravelledFabric = ModBlocks.UNRAVELLED_FABRIC.getDefaultState();
        // Build the gateway out of Unraveled Fabric. Since nearly all the blocks in Limbo are of
        // that type, there is no point replacing the ground.
        world.setBlockState(pos.add(0, 3, 1), unravelledFabric, 2);
        world.setBlockState(pos.add(0, 3, -1), unravelledFabric, 2);

        // Build the columns around the door
        world.setBlockState(pos.add(0, 2, -1), unravelledFabric, 2);
        world.setBlockState(pos.add(0, 2, 1), unravelledFabric, 2);
        world.setBlockState(pos.add(0, 1, 1), unravelledFabric, 2);
        world.setBlockState(pos.add(0, 1, 1), unravelledFabric, 2);

        this.placePortal(world, pos.add(0, 1, 0), Direction.NORTH);
    }

    private void placePortal(StructureWorldAccess world, BlockPos pos, Direction facing) {
        world.setBlockState(pos, ModBlocks.DIMENSIONAL_PORTAL.getDefaultState(), 2);
    }

    @Override
    public boolean isLocationValid(World world, BlockPos pos) {
        return ModDimensions.isLimboDimension(world);
    }
}
