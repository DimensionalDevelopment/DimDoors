package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class ItemDimensionalDoor extends BaseItemDoor
{
	public ItemDimensionalDoor(int itemID, Material material)
	{
		super(itemID, material);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add("Place on the block under a rift");
		par3List.add("to activate that rift or place");
		par3List.add("anywhere else to create a");
		par3List.add("pocket dimension.");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (tryPlacingDoor(mod_pocketDim.dimensionalDoor, world, player, stack) &&
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
		return tryItemUse(mod_pocketDim.dimensionalDoor, stack, player, world, x, y, z, par7, false, true);
	}
}