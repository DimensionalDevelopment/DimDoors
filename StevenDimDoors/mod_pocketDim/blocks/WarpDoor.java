package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WarpDoor extends DDoorBase implements IDDoorLogic
{
	private Icon blockIconBottom;

	public WarpDoor(int blockID, Material material) 
	{
		super(blockID, material);
	}

	@Override
	public void placeDimDoor(World world, int x, int y, int z) 
	{
		if (!world.isRemote && world.getBlockId(x, y - 1, z) == this.blockID)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null&&dimension.isPocketDimension())
			{
				dimension.createLink(x, y, z, LinkTypes.SAFE_EXIT);
			}
		}
	}
	
	@Override
	public int getDrops()
	{
		return Item.doorWood.itemID;
	}

	
}