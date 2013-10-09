package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityRift;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class itemRiftRemover extends Item
{
	public itemRiftRemover(int itemID, Material par2Material)
	{
		super(itemID);
		this.setMaxStackSize(1);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		this.setMaxDamage(4);
	}

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		/**
		 * Im not exactly sure why this was done with onItemFirstUse before, but that is what was causing the issue. 
		 * OnItemRightClick would only be called if there was no block to be clicked on, and because we never actually click on rifts (instead we raytrace them) 
		 * we need a method that is always called. We can update other clients of the visual information using the rift TE, by having it send a packet to all players in a radius. 
		 * I will need to look at the new network code first, though. 
		 */
		//Raytrace for rift block because they dont have any collision
		MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(world, player, true);
		if (hit != null)
		{
			int hx = hit.blockX;
			int hy = hit.blockY;
			int hz = hit.blockZ;
			NewDimData dimension = PocketManager.getDimensionData(world);
			DimLink link = dimension.getLink(hx, hy, hz);
			if (world.getBlockId(hx, hy, hz) == mod_pocketDim.blockRift.blockID && link != null &&
				player.canPlayerEdit(hx, hy, hz, hit.sideHit, stack))
			{
				// Tell the rift's tile entity to do its removal animation
				// Handle server client stuff on the rift TE
				TileEntity tileEntity = world.getBlockTileEntity(hx, hy, hz);
				if (tileEntity != null && tileEntity instanceof TileEntityRift)
				{
					((TileEntityRift) tileEntity).shouldClose = true;	
				}
				if (!player.capabilities.isCreativeMode)
				{
					stack.damageItem(1, player);
				}
				player.worldObj.playSoundAtEntity(player, "mods.DimDoors.sfx.riftClose", 0.8f, 1);
				
			}
		}
		return stack;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		return false;
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		par3List.add("Use near exposed rift");
		par3List.add("to remove it and");
		par3List.add("any nearby rifts.");
	}
}
