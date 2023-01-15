package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;

public interface IRiftProvider<T extends TileEntityRift> extends ITileEntityProvider {
    T getRift(World world, BlockPos pos, IBlockState state);
}
