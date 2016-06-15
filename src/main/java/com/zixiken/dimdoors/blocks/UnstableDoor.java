package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.NewDimData;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.mod_pocketDim;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
	public void placeLink(World world, int x, int y, int z) 
	{
		if (!world.isRemote && world.getBlock(x, y - 1, z) == this)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			dimension.createLink(x, y, z, LinkType.RANDOM,world.getBlockMetadata(x, y - 1, z));
		}
	}
	
	@Override
	public Item getDoorItem()
	{
		return mod_pocketDim.itemUnstableDoor;
	}
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return Items.iron_door;
	}
}