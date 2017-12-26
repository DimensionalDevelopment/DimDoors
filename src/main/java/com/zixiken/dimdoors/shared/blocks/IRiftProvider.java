package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.shared.VirtualLocation;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import ddutils.Location;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRiftProvider<T extends TileEntityRift> extends ITileEntityProvider {

    // This returns whether that block is the block containg the rift. If the rift entity does not exist, it must be created
    public T getRift(World world, BlockPos pos, IBlockState state); // TODO: split this to superinterface IHasRiftEntity?

    public void setupRift(T rift);

    public boolean hasTileEntity(IBlockState state);

    @Override
    public T createNewTileEntity(World world, int meta);

    // Call only once per structure (on item place)!
    public default void handleRiftSetup(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote) return;
        T rift = getRift(world, pos, state);

        // Set the rift's virtual position
        rift.setVirtualLocation(VirtualLocation.fromLocation(new Location(world, pos)));

        // Configure the rift to its default functionality
        setupRift(rift);

        // Set the tile entity and register it
        //world.setTileEntity(pos, rift);
        rift.markDirty();
        rift.register();
        T rift2 = getRift(world, pos, state);
    }
}
