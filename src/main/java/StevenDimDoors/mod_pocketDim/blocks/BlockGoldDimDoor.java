package StevenDimDoors.mod_pocketDim.blocks;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoorGold;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGoldDimDoor extends BaseDimDoor
{

	public BlockGoldDimDoor(Material material, DDProperties properties)
	{
		super( material, properties);
	}

	@Override
	public void placeLink(World world, int x, int y, int z) 
	{
		if (!world.isRemote && world.getBlock(x, y - 1, z) == this)
		{
			NewDimData dimension = PocketManager.createDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null)
			{
				dimension.createLink(x, y, z, LinkType.POCKET,world.getBlockMetadata(x, y - 1, z));
			}
		}
	}
	
	@Override
	public Item getDoorItem()
	{
		return mod_pocketDim.itemGoldenDimensionalDoor;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityDimDoorGold();
	}

}
