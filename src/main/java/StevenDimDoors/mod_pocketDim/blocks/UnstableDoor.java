package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

import java.util.ArrayList;
import java.util.Random;

public class UnstableDoor extends BaseDimDoor
{
	public UnstableDoor(Material material, DDProperties properties)
	{
		super(material, properties);
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