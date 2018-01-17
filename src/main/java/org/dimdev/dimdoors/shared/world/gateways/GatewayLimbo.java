package org.dimdev.dimdoors.shared.world.gateways;

import org.dimdev.dimdoors.shared.blocks.BlockFabric;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GatewayLimbo extends BaseGateway {
    @Override
    public void generate(World world, int x, int y, int z)
    {
        IBlockState limbo = ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.TYPE, BlockFabric.EnumType.UNRAVELED);
        // Build the gateway out of Unraveled Fabric. Since nearly all the blocks in Limbo are of
        // that type, there is no point replacing the ground.
        world.setBlockState(new BlockPos(x, y + 3, z + 1), limbo);
        world.setBlockState(new BlockPos(x, y + 3, z - 1), limbo);

        // Build the columns around the door
        world.setBlockState(new BlockPos(x, y + 2, z - 1), limbo);
        world.setBlockState(new BlockPos(x, y + 2, z + 1), limbo);
        world.setBlockState(new BlockPos(x, y + 1, z - 1), limbo);
        world.setBlockState(new BlockPos(x, y + 1, z + 1), limbo);

        ItemDoor.placeDoor(world, new BlockPos(x, y + 1, z), EnumFacing.getHorizontal(0), ModBlocks.TRANSIENT_DIMENSIONAL_DOOR, false);
    }

    @Override
    public boolean isLocationValid(World world, int x, int y, int z) {
        return world.provider instanceof WorldProviderLimbo;
    }
}
