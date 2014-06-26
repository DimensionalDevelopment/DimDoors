package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStabilizedRiftSignature extends ItemRiftSignature
{
	public ItemStabilizedRiftSignature(int itemID)
	{
		super(itemID);
	}

	@Override
	public void registerIcons(IconRegister par1IconRegister)
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
		Point4DOrientation source = getSource(stack);
		
		// Check if the Stabilized Rift Signature has been initialized
		int orientation = MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;
		if (source != null)
		{
			// Yes, it's initialized.
			DimLink link;
			DimLink reverse;
			NewDimData sourceDimension = PocketManager.getDimensionData(source.getDimension());
			NewDimData destinationDimension = PocketManager.getDimensionData(world);
			
			// Check whether the SRS is being used to restore one of its previous
			// link pairs. In other words, the SRS is being used on a location
			// that already has a link pointing to the SRS's source, with the
			// intention of overwriting the source-side link to point there.
			// Those benign redirection operations will be handled for free.
			
			if (false) //TODO Add proper check!
			{
				// Only the source-to-destination link is needed.
				link = sourceDimension.createLink(source.getX(), source.getY(), source.getZ(), LinkTypes.NORMAL, source.getOrientation());
				destinationDimension.setDestination(link, x, adjustedY, z);
			}
			else
			{
				// Check if the player is in creative mode,
				// or if the player can pay with an Ender Pearl to create a rift.
				if (!player.capabilities.isCreativeMode &&
						!player.inventory.consumeInventoryItem(Item.enderPearl.itemID))
				{
					mod_pocketDim.sendChat(player, "You don't have any Ender Pearls!");
					// I won't do this, but this is the chance to localize chat 
					// messages sent to the player; look at ChatMessageComponent 
					// and how MFR does it with items like the safari net launcher
					return true;
				}
	
				// Create links connecting the two endpoints.
				link = sourceDimension.createLink(source.getX(), source.getY(), source.getZ(), LinkTypes.NORMAL, source.getOrientation());
				reverse = destinationDimension.createLink(x, adjustedY, z, LinkTypes.NORMAL, orientation);
				destinationDimension.setDestination(link, x, adjustedY, z);
				sourceDimension.setDestination(reverse, source.getX(), source.getY(), source.getZ());
	
				// Try placing a rift at the destination point
				if (!mod_pocketDim.blockRift.isBlockImmune(world, x, adjustedY, z))
				{
					world.setBlock(x, adjustedY, z, mod_pocketDim.blockRift.blockID);
				}
			}

			// Try placing a rift at the source point, but check if its world is loaded first
			World sourceWorld = DimensionManager.getWorld(sourceDimension.id());
			if (sourceWorld != null &&
				!mod_pocketDim.blockRift.isBlockImmune(sourceWorld, source.getX(), source.getY(), source.getZ()))
			{
				sourceWorld.setBlock(source.getX(), source.getY(), source.getZ(), mod_pocketDim.blockRift.blockID);
			}
			
			mod_pocketDim.sendChat(player, "Rift Created");
			world.playSoundAtEntity(player, "mods.DimDoors.sfx.riftEnd", 0.6f, 1);
		}
		else
		{
			// The link signature has not been used. Store its current target as the first location. 
			setSource(stack, x, adjustedY, z, orientation, PocketManager.getDimensionData(world));
			mod_pocketDim.sendChat(player,"Location Stored in Stabilized Rift Signature");
			world.playSoundAtEntity(player,"mods.DimDoors.sfx.riftStart", 0.6f, 1);
		}
		return true;
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
			par3List.add("Leads to (" + source.getX() + ", " + source.getY() + ", " + source.getZ() + ") at dimension #" + source.getDimension());
		}
		else
		{
			par3List.add("First click stores a location,");
			par3List.add("other clicks create rifts linking");
			par3List.add("the first and last locations together.");
		}
	}
}
