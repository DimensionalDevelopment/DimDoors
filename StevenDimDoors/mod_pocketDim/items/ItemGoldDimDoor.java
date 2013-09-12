package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class ItemGoldDimDoor extends BaseItemDoor
{

	public ItemGoldDimDoor(int itemID, Material material) {
		super(itemID, material);
		// TODO Auto-generated constructor stub
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add("Similar to a Iron Dim Door");
		par3List.add("But if present in a pocket dim");
		par3List.add("it will keep it loaded.");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y,
		int z, int par7, float par8, float par9, float par10)
	{
		return tryItemUse(mod_pocketDim.goldDimDoor, stack, player, world, x, y, z, par7, false, true);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (tryPlacingDoor(mod_pocketDim.goldDimDoor, world, player, stack) &&
				!player.capabilities.isCreativeMode)
			{
				stack.stackSize--;
			}
		}
		return stack;
	}
	
	

}
