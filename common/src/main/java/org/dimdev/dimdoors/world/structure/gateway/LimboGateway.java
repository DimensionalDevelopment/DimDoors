package org.dimdev.dimdoors.world.structure.gateway;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.WaterLoggableBlockWithEntity;
import org.dimdev.dimdoors.world.ModDimensions;

public enum LimboGateway implements Gateway {
    INSTANCE;

    @Override
    public void generate(WorldGenLevel world, BlockPos pos) {
        if (!this.isLocationValid(world, pos)) {
            return;
        }
        BlockState unravelledFabric = ModBlocks.UNRAVELLED_FABRIC.get().defaultBlockState();
        // Build the gateway out of Unraveled Fabric. Since nearly all the blocks in Limbo are of
        // that type, there is no point replacing the ground.
        world.setBlock(pos.offset(1, 3, 0), unravelledFabric, 2);
        world.setBlock(pos.offset(-1, 3, 0), unravelledFabric, 2);


        // Build the columns around the door
        world.setBlock(pos.offset(-1, 2, 0), unravelledFabric, 2);
        world.setBlock(pos.offset(1, 2, 0), unravelledFabric, 2);
        world.setBlock(pos.offset(1, 1, 0), unravelledFabric, 2);
        world.setBlock(pos.offset(1, 1, 0), unravelledFabric, 2);

        world.setBlock(pos.offset(-1, 0, 0), unravelledFabric, 2);
        world.setBlock(pos.offset(0, 0, 0), unravelledFabric, 2);
        world.setBlock(pos.offset(1, 0, 0), unravelledFabric, 2);

        this.placePortal(world, pos.offset(0, 1, 0), Direction.NORTH);
    }

    @Override
    public boolean isLocationValid(WorldGenLevel world, BlockPos pos) {
        return ModDimensions.isLimboDimension(world.getLevel());
    }

    private void placePortal(WorldGenLevel world, BlockPos pos, Direction facing) {
        world.setBlock(pos, ModBlocks.DIMENSIONAL_PORTAL.get().defaultBlockState().setValue(WaterLoggableBlockWithEntity.WATERLOGGED, false), 2);
    }
}
