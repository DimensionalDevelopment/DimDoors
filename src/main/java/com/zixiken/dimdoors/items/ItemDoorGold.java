package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDoorGold extends ItemDoor {
	public static final String ID = "itemDoorGold";

	public ItemDoorGold() {
		super(ModBlocks.blockDoorGold);
		setMaxStackSize(16);
        setUnlocalizedName(ID);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
            EnumFacing side, float hitX, float hitY, float hitZ) {
		if (side == EnumFacing.UP) {
            Block doorBlock = ModBlocks.blockDoorGold;
			if(!worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)) pos = pos.up();

			if (playerIn.canPlayerEdit(pos, side, stack) &&
                    playerIn.canPlayerEdit(pos.up(), side, stack) &&
                    doorBlock.canPlaceBlockAt(worldIn, pos)) {
                placeDoor(worldIn, pos, EnumFacing.fromAngle(playerIn.rotationYaw), doorBlock);
				--stack.stackSize;
				return true;
			}
		}
        return false;
	}
}
