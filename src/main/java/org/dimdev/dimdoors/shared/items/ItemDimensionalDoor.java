package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.shared.blocks.IRiftProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;

public abstract class ItemDimensionalDoor extends ItemDoor {

    public <T extends Block & IRiftProvider<?>> ItemDimensionalDoor(T block) {
        super(block);
    }

    // TODO: endermen/block placers should set up blocks too, but this method doesn't get called when they place the block
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return EnumActionResult.FAIL;
        boolean replaceable = world.getBlockState(pos).getBlock().isReplaceable(world, pos);

        // Store the rift entity if there's a rift block there that may be broken
        TileEntityFloatingRift rift = null;
        if (world.getBlockState(replaceable ? pos : pos.offset(facing)).getBlock().equals(ModBlocks.RIFT)) {
            rift = (TileEntityFloatingRift) world.getTileEntity(replaceable ? pos : pos.offset(facing));
            rift.setUnregisterDisabled(true);
        }

        EnumActionResult result = super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        if (!replaceable) pos = pos.offset(facing);
        if (result == EnumActionResult.SUCCESS) {
            IBlockState state = world.getBlockState(pos);
            if (rift == null) {
                ((IRiftProvider<?>) state.getBlock()).handleRiftSetup(world, pos, state);
            } else {
                // Copy from the old rift
                TileEntityEntranceRift newRift = (TileEntityEntranceRift) world.getTileEntity(pos);
                newRift.copyFrom(rift);
                newRift.updateAvailableLinks();
            }
        } else if (rift != null) {
            rift.setUnregisterDisabled(false);
        }
        return result;
    }
}
