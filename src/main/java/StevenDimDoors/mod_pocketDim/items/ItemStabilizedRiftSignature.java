package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStabilizedRiftSignature extends ItemRiftSignature
{
	public ItemStabilizedRiftSignature()
	{
		super();
	}

	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		// Return false on the client side to pass this request to the server
		if (world.isRemote)
		{
			return false;
		}

		// Adjust Y so the rift is at head level, depending on the presence of certain blocks
		int adjustedY = adjustYForSpecialBlocks(world, x, y + 2, z);
		if (!player.canPlayerEdit(x, adjustedY, z, side, stack))
		{
			return true;
		}
		int orientation = MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;
		
		// Check if the Stabilized Rift Signature has been initialized
		Point4DOrientation source = getSource(stack);
		if (source != null)
		{

			// Yes, it's initialized.
			NewDimData sourceDimension = PocketManager.getDimensionData(source.getDimension());
			NewDimData destinationDimension = PocketManager.createDimensionData(world);
			DimLink reverse = destinationDimension.getLink(x, adjustedY, z);
			DimLink link;
			
			// Check whether the SRS is being used to restore one of its previous
			// link pairs. In other words, the SRS is being used on a location
			// that already has a link pointing to the SRS's source, with the
			// intention of overwriting the source-side link to point there.
			// Those benign redirection operations will be handled for free.
			
			if (reverse != null && source.getPoint().equals(reverse.destination()))
			{
				// Only the source-to-destination link is needed.
				link = sourceDimension.createLink(source.getX(), source.getY(), source.getZ(), LinkType.NORMAL, source.getOrientation());
				destinationDimension.setLinkDestination(link, x, adjustedY, z);
			}
			else
			{
				// Check if the player is in creative mode,
				// or if the player can pay with an Ender Pearl to create a rift.
				if (!player.capabilities.isCreativeMode &&
						!player.inventory.consumeInventoryItem(Items.ender_pearl))
				{
					mod_pocketDim.sendChat(player, "You don't have any Ender Pearls!");
					// I won't do this, but this is the chance to localize chat 
					// messages sent to the player; look at ChatMessageComponent 
					// and how MFR does it with items like the safari net launcher
					return true;
				}
	
				// Create links connecting the two endpoints.
				link = sourceDimension.createLink(source.getX(), source.getY(), source.getZ(), LinkType.NORMAL, source.getOrientation());
				reverse = destinationDimension.createLink(x, adjustedY, z, LinkType.NORMAL, orientation);
				destinationDimension.setLinkDestination(link, x, adjustedY, z);
				sourceDimension.setLinkDestination(reverse, source.getX(), source.getY(), source.getZ());
	
				// Try placing a rift at the destination point
				mod_pocketDim.blockRift.tryPlacingRift(world, x, adjustedY, z);
			}
			
			// Try placing a rift at the source point
			// We don't need to check if sourceWorld is null - that's already handled.
			World sourceWorld = DimensionManager.getWorld(sourceDimension.id());
			
			mod_pocketDim.blockRift.tryPlacingRift(sourceWorld, source.getX(), source.getY(), source.getZ());
			mod_pocketDim.sendChat(player, "Rift Created");
			world.playSoundAtEntity(player, "mods.DimDoors.sfx.riftEnd", 0.6f, 1);
		}
		else
		{
			// The link signature has not been used. Store its current target as the first location. 
			setSource(stack, x, adjustedY, z, orientation, PocketManager.createDimensionData(world));
			mod_pocketDim.sendChat(player, "Location Stored in Stabilized Rift Signature");
			world.playSoundAtEntity(player, "mods.DimDoors.sfx.riftStart", 0.6f, 1);
		}
		return true;
	}

	public static boolean useFromDispenser(ItemStack stack, World world, int x, int y, int z)
	{
		// Stabilized Rift Signatures can only be used from dispensers to restore
		// a previous link pair. The operation would be free for a player, so
		// dispensers can also perform it for free. Otherwise, the item does nothing.
		if (world.isRemote)
		{
			return false;
		}
		
		// Adjust Y so the rift is at head level, depending on the presence of certain blocks
		int adjustedY = adjustYForSpecialBlocks(world, x, y + 2, z);
		Point4DOrientation source = getSource(stack);
		
		// The SRS must have been initialized
		if (source != null)
		{
			NewDimData sourceDimension = PocketManager.getDimensionData(source.getDimension());
			NewDimData destinationDimension = PocketManager.createDimensionData(world);
			DimLink reverse = destinationDimension.getLink(x, adjustedY, z);
			DimLink link;
			
			// Check whether the SRS is being used to restore one of its previous
			// link pairs. In other words, the SRS is being used on a location
			// that already has a link pointing to the SRS's source, with the
			// intention of overwriting the source-side link to point there.
			if (reverse != null && source.getPoint().equals(reverse.destination()))
			{
				// Only the source-to-destination link is needed.
				link = sourceDimension.createLink(source.getX(), source.getY(), source.getZ(), LinkType.NORMAL, source.getOrientation());
				destinationDimension.setLinkDestination(link, x, adjustedY, z);
				
				// Try placing a rift at the source point
				// We don't need to check if sourceWorld is null - that's already handled.
				World sourceWorld = DimensionManager.getWorld(sourceDimension.id());
				mod_pocketDim.blockRift.tryPlacingRift(sourceWorld, source.getX(), source.getY(), source.getZ());
				
				// This call doesn't seem to be working...
				world.playSoundEffect(x + 0.5, adjustedY + 0.5, z + 0.5, "mods.DimDoors.sfx.riftEnd", 0.6f, 1);
				return true;
			}
		}
		return false;
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		Point4DOrientation source = getSource(par1ItemStack);
		if (source != null)
		{
            String text = StatCollector.translateToLocalFormatted("info.riftSignature.bound", source.getX(), source.getY(), source.getZ(), source.getDimension());
			par3List.add(text);
		}
		else
		{
			mod_pocketDim.translateAndAdd("info.riftSignature.stable", par3List);
		}
	}
}
