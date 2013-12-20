package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class ItemWarpDoor extends BaseItemDoor
{
	public ItemWarpDoor(int itemID, Material material)
	{
		super(itemID, material);
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
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (tryPlacingDoor(mod_pocketDim.warpDoor, world, player, stack) &&
				!player.capabilities.isCreativeMode)
			{
				stack.stackSize--;
			}
		}
		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y,
		int z, int par7, float par8, float par9, float par10)
	{
		return tryItemUse(mod_pocketDim.warpDoor, stack, player, world, x, y, z, par7, false, true);
	}
}