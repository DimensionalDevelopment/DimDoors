package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.blocks.BaseDimDoor;

public class ItemGoldDimDoor extends BaseItemDoor
{

	public ItemGoldDimDoor(Material material, ItemDoor door)
    {
  	  super(material, door);
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add("Similar to a Dimensional Door");
		par3List.add("but keeps a pocket dimension");
		par3List.add("loaded if placed on the inside.");
	}

	@Override
	protected BaseDimDoor getDoorBlock()
	{
		return (BaseDimDoor) mod_pocketDim.goldenDimensionalDoor;
	}
}
