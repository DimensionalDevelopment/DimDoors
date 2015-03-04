package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.blocks.BaseDimDoor;

public class ItemPersonalDoor extends BaseItemDoor
{
	public ItemPersonalDoor(Material material, ItemDoor door)
    {
  	  super(material, door);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add("Creates a pathway to");
		par3List.add("Your personal pocket");

	
	}

	@Override
	protected BaseDimDoor getDoorBlock()
	{
		return (BaseDimDoor) mod_pocketDim.personalDimDoor;
	}
}