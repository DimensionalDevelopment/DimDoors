package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.shared.blocks.IRiftProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

public abstract class ItemDimensionalTrapdoor extends ItemBlock { // TODO: Iron dimensional trapdoor

    public <T extends Block & IRiftProvider<TileEntityEntranceRift>>ItemDimensionalTrapdoor(T block) {
        super(block);
    }

    // TODO: placing trapdoors on rifts, merge this code with the dimdoor code/common interface
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean replaceable = world.getBlockState(pos).getBlock().isReplaceable(world, pos); // Check this before calling super, since that changes the block
        EnumActionResult result = super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        if (result == EnumActionResult.SUCCESS) {
            if (!replaceable) pos = pos.offset(facing);
            IBlockState state = world.getBlockState(pos);
            // Get the rift entity (not hard coded, works with any door size)
            @SuppressWarnings("unchecked") // Guaranteed to be IRiftProvider<TileEntityEntranceRift> because of constructor
            TileEntityEntranceRift entranceRift = ((IRiftProvider<TileEntityEntranceRift>) state.getBlock()).getRift(world, pos, state);

            // Configure the rift to its default functionality
            setupRift(entranceRift);

            // Register the rift in the registry
            entranceRift.markDirty();
            entranceRift.register();
        }
        return result;
    }

    protected abstract void setupRift(TileEntityEntranceRift entranceRift); // TODO: NBT-based
}
