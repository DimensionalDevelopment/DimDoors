package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.blocks.BaseDimDoor;

public class ItemWarpDoor extends BaseItemDoor
{
	public ItemWarpDoor(Material material, ItemDoor door)
	{
		super(material, door);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add("Place on the block under");
		par3List.add("a rift to create a portal,");
		par3List.add("or place anywhere in a");
		par3List.add("pocket dimension to exit.");
	}
    
	@Override
	protected BaseDimDoor getDoorBlock()
	{
		return (BaseDimDoor) mod_pocketDim.warpDoor;
	}
}