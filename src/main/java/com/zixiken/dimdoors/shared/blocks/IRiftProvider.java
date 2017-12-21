package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import com.zixiken.dimdoors.shared.util.Location;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRiftProvider<T extends TileEntityRift> extends ITileEntityProvider {

    public T getRift(World world, BlockPos pos, IBlockState state); // TODO: split this to superinterface IHasRiftEntity?

    public void setupRift(T rift);

    public boolean hasTileEntity(IBlockState state);

    @Override
    public T createNewTileEntity(World world, int meta);

    public default void handleRiftPlaced(World world, BlockPos pos, IBlockState state) {
        if (hasTileEntity(state) && !DimDoors.disableRiftSetup) { // TODO: better check for disableRiftSetup (support other plugins such as WorldEdit, support doors being placed while schematics are being placed)
            T rift = createNewTileEntity(world, state.getBlock().getMetaFromState(state));

            // Set the rift's virtual position
            rift.setVirtualLocation(PocketRegistry.locationToVirtualLocation(new Location(rift.getWorld(), rift.getPos())));

            // Configure the rift to its default functionality
            setupRift(rift);

            // Set the tile entity and register it
            world.setTileEntity(pos, rift);
            rift.markDirty();
            rift.register();
        }
    }
}
