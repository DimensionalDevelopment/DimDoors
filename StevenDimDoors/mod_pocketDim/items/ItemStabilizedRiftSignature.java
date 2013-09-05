package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStabilizedRiftSignature extends ItemRiftSignature
{
	public ItemStabilizedRiftSignature(int itemID)
	{
		super(itemID);
	}

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

		// We don't check for replaceable blocks. The user can deal with that. <_<
		y += 2; //Increase y by 2 to place the rift at head level
		if (!player.canPlayerEdit(x, y, z, 0, stack))
		{
			return true;
		}

		// Check if the Stabilized Rift Signature has been initialized
		Point4D source = getSource(stack);
		if (source != null)
		{
			// Yes, it's initialized. Check if the player is in creative
			// or if the player can pay an Ender Pearl to create a rift.
			if (!player.capabilities.isCreativeMode && !player.inventory.hasItem(Item.enderPearl.itemID))
			{
				player.sendChatToPlayer("You don't have any Ender Pearls!");
				return true;
			}

			//The link was used before and already has an endpoint stored. Create links connecting the two endpoints.
			NewDimData sourceDimension = PocketManager.getDimensionData(source.getDimension());
			NewDimData destinationDimension = PocketManager.getDimensionData(world);
			DimLink link = sourceDimension.createLink(source.getX(), source.getY(), source.getZ(), LinkTypes.NORMAL);
			DimLink reverse = destinationDimension.createLink(x, y, z, LinkTypes.NORMAL);
			destinationDimension.setDestination(link, x, y, z);
			sourceDimension.setDestination(reverse, source.getX(), source.getY(), source.getZ());

			//Try placing a rift at the destination point
			if (!mod_pocketDim.blockRift.isBlockImmune(world, x, y, z))
			{
				world.setBlock(x, y, z, mod_pocketDim.blockRift.blockID);
			}

			//Try placing a rift at the source point, but check if its world is loaded first
			World sourceWorld = DimensionManager.getWorld(sourceDimension.id());
			if (sourceWorld != null &&
				!mod_pocketDim.blockRift.isBlockImmune(sourceWorld, source.getX(), source.getY(), source.getZ()))
			{
				sourceWorld.setBlock(source.getX(), source.getY(), source.getZ(), mod_pocketDim.blockRift.blockID);
			}

			if (!player.capabilities.isCreativeMode)
			{
				player.inventory.consumeInventoryItem(Item.enderPearl.itemID);
			}
			player.sendChatToPlayer("Rift Created");
			world.playSoundAtEntity(player,"mods.DimDoors.sfx.riftEnd", 0.6f, 1);
		}
		else
		{
			//The link signature has not been used. Store its current target as the first location. 
			setSource(stack, x, y, z, PocketManager.getDimensionData(world));
			player.sendChatToPlayer("Location Stored in Rift Signature");
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
		Point4D source = getSource(par1ItemStack);
		if (source != null)
		{
			par3List.add("Leads to (" + source.getX() + ", " + source.getY() + ", " + source.getZ() + ") at dimension #" + source.getDimension());
		}
		else
		{
			par3List.add("First click stores a location,");
			par3List.add("second click creates two rifts");
			par3List.add("that link the locations together.");
		}
	}
}
