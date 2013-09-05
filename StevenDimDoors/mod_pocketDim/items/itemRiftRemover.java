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
		// We invoke PlayerControllerMP.onPlayerRightClick() from here so that Minecraft
		// will invoke onItemUseFirst() on the client side. We'll tell it to pass the
		// request to the server, which will make sure that rift-related changes are
		// reflected on the server.

		if (!world.isRemote)
		{
			return stack;
		}

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
				// Invoke onPlayerRightClick()
				FMLClientHandler.instance().getClient().playerController.onPlayerRightClick(
					player, world, stack, hx, hy, hz, hit.sideHit, hit.hitVec);
			}
		}
		return stack;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		// We want to use onItemUseFirst() here so that this code will run on the server side,
		// so we don't need the client to send link-related updates to the server. Still,
		// check whether we have a rift in sight before passing the request over.

		// On integrated servers, the link won't be removed immediately because of the rift
		// removal animation. That means we'll have a chance to check for the link before
		// it's deleted. Otherwise the Rift Remover's durability wouldn't drop.
		
		NewDimData dimension = PocketManager.getDimensionData(world);
		DimLink link = dimension.getLink(x, y, z);
		if (world.getBlockId(x, y, z) == mod_pocketDim.blockRift.blockID && link != null &&
			player.canPlayerEdit(x, y, z, side, stack))
		{
			// Tell the rift's tile entity to do its removal animation
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			if (tileEntity != null && tileEntity instanceof TileEntityRift)
			{
				((TileEntityRift) tileEntity).shouldClose = true;
			}
			else if (!world.isRemote)
			{
				// Only set the block to air on the server side so that we don't
				// tell the server to remove the rift block before it can use the
				// Rift Remover. Otherwise, it won't know to reduce durability.
				world.setBlockToAir(x, y, z);
			}
			if (world.isRemote)
			{
				// Tell the server about this
				return false;
			}
			else
			{
				if (!player.capabilities.isCreativeMode)
				{
					stack.damageItem(1, player);
				}
				player.worldObj.playSoundAtEntity(player, "mods.DimDoors.sfx.riftClose", 0.8f, 1);
			}
		}
		return true;
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
