package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoor;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoorGold;

public class BlockGoldDimDoor extends BaseDimDoor implements IDimDoor
{

	public BlockGoldDimDoor(int blockID, Material material,
			DDProperties properties) {
		super(blockID, material, properties);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void placeLink(World world, int x, int y, int z) 
	{
		if (!world.isRemote && world.getBlockId(x, y - 1, z) == this.blockID)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null)
			{
				dimension.createLink(x, y, z, LinkTypes.POCKET,world.getBlockMetadata(x, y - 1, z));
			}
		}
		
	}
	@Override
	public int getDrops()
	{	
		return mod_pocketDim.itemGoldDoor.itemID;
	}	
	
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityDimDoorGold();
	}

}
