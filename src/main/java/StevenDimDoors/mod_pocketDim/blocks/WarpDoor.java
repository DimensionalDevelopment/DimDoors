package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class WarpDoor extends BaseDimDoor
{
	public WarpDoor(Material material, DDProperties properties)
	{
		super(material, properties);
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