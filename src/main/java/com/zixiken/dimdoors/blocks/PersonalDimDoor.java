package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.world.PersonalPocketProvider;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.mod_pocketDim;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import com.zixiken.dimdoors.core.NewDimData;

public class PersonalDimDoor extends BaseDimDoor {
	public static final String ID = "dimDoorPersonal";

	public PersonalDimDoor() {
		super(Material.rock);
        setHardness(0.1F);
        setUnlocalizedName(ID);
	}

	@Override
	public void placeLink(World world, int x, int y, int z)
	{
		if (!world.isRemote && world.getBlock(x, y - 1, z) == this)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null)
			{
                if (world.provider instanceof PersonalPocketProvider)
                    dimension.createLink(x, y, z, LinkType.LIMBO, world.getBlockMetadata(x, y-1, z));
                else
				    dimension.createLink(x, y, z, LinkType.PERSONAL, world.getBlockMetadata(x, y - 1, z));
			}
		}
	}

	@Override
	public Item getDoorItem()
	{
		return mod_pocketDim.itemPersonalDoor;
	}

}
