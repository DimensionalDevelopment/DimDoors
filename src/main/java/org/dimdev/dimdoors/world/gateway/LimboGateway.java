package org.dimdev.dimdoors.world.gateway;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LimboGateway extends BaseGateway {
    @Override
    public void generate(World world, int x, int y, int z) {
        BlockState unravelledFabric = ModBlocks.UNRAVELLED_FABRIC.getDefaultState();
        // Build the gateway out of Unraveled Fabric. Since nearly all the blocks in Limbo are of
        // that type, there is no point replacing the ground.
        world.setBlockState(new BlockPos(x, y + 3, z + 1), unravelledFabric);
        world.setBlockState(new BlockPos(x, y + 3, z - 1), unravelledFabric);

        // Build the columns around the door
        world.setBlockState(new BlockPos(x, y + 2, z - 1), unravelledFabric);
        world.setBlockState(new BlockPos(x, y + 2, z + 1), unravelledFabric);
        world.setBlockState(new BlockPos(x, y + 1, z - 1), unravelledFabric);
        world.setBlockState(new BlockPos(x, y + 1, z + 1), unravelledFabric);

        placePortal(world, new BlockPos(x, y + 1, z), Direction.NORTH);
    }

    private void placePortal(World world, BlockPos pos, Direction facing) {
        // todo
    }

    @Override
    public boolean isLocationValid(World world, int x, int y, int z) {
        return ModDimensions.isLimboDimension(world);
    }
}
