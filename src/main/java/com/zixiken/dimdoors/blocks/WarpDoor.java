package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.mod_pocketDim;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import com.zixiken.dimdoors.core.NewDimData;

public class WarpDoor extends BaseDimDoor {
    public static final String ID = "dimDoorWarp";

	public WarpDoor() {
		super(Material.wood);
        setHardness(1.0F);
        setUnlocalizedName(ID);
	}

	@Override
	public void placeLink(World world, int x, int y, int z) 
	{
		if (!world.isRemote && world.getBlock(x, y - 1, z) == this)
		{
			NewDimData dimension = PocketManager.createDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null && dimension.isPocketDimension())
			{
				dimension.createLink(x, y, z, LinkType.SAFE_EXIT,world.getBlockMetadata(x, y - 1, z));
			}
		}
	}
	
	@Override
	public Item getDoorItem()
	{
		return mod_pocketDim.itemWarpDoor;
	}
}