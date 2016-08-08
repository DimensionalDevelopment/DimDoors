package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.DimData;
import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class UnstableDoor extends BaseDimDoor {
	public static final String ID = "chaosDoor";

	public UnstableDoor() {
		super(Material.iron);
        setHardness(.2F);
        setUnlocalizedName(ID);
        setLightLevel(.0F);
	}

	@Override
	public void placeLink(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos.down());
		if (!world.isRemote && state.getBlock() == this) {
			DimData dimension = PocketManager.getDimensionData(world);
			dimension.createLink(pos, LinkType.RANDOM, state.getValue(BlockDoor.FACING));
		}
	}
	
	@Override
	public Item getDoorItem() {return DimDoors.itemUnstableDoor;}

    @Override
	public Item getItemDropped(IBlockState state, Random random, int fortune) {return Items.iron_door;}
}