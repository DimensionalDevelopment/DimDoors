package com.zixiken.dimdoors.items.behaviors;

import com.zixiken.dimdoors.items.ItemStabilizedRiftSignature;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DispenserBehaviorStabilizedRS extends BehaviorDefaultDispenseItem {
    @Override
	public ItemStack dispenseStack(IBlockSource dispenser, ItemStack stack) {
    	// Search for a non-air block up to 3 blocks in front of a dispenser.
    	// If it's found, call ItemStabilizedRiftSignature.useFromDispenser().
        BlockPos pos = dispenser.getBlockPos();
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        EnumFacing facing = BlockDispenser.getFacing(dispenser.getBlockMetadata());
        int dx = facing.getFrontOffsetX();
        int dy = facing.getFrontOffsetY();
        int dz = facing.getFrontOffsetZ();
        World world = dispenser.getWorld();
        
        for (int k = 1; k <= 3; k++) {
        	x += dx;
        	y += dy;
        	z += dz;
            pos = new BlockPos(x, y, z);
        	if (!world.isAirBlock(pos)) {
        		// Found a block. Activate the item.
        		ItemStabilizedRiftSignature.useFromDispenser(stack, world, pos);
        		break;
        	}
        }
        // The item stack isn't modified
        return stack;
    }
}
