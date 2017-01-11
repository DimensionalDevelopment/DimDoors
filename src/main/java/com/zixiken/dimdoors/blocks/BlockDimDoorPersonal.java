package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDimDoorPersonal extends BlockDimDoorBase {
	public static final String ID = "blockDimDoorPersonal";

	public BlockDimDoorPersonal() {
		super(Material.ROCK);
        setHardness(0.1F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
	}

	@Override
	public void placeLink(World world, BlockPos pos) {
	}

	@Override
	public Item getItemDoor() {return ModItems.itemDimDoorPersonal;}

}
