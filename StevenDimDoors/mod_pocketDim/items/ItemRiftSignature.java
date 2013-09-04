package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

public class ItemRiftSignature extends Item
{
	public ItemRiftSignature(int itemID)
	{
		super(itemID);
		this.setMaxStackSize(1);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		this.setMaxDamage(0);
		this.hasSubtypes = true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		//Make the item glow if it has one endpoint stored
		return (stack.getItemDamage() != 0);
	}

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		tryItemUse(stack, player, world, x, y, z);
		return true;
	}
	
	protected boolean tryItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z)
	{
		if (!world.isRemote)
		{
			//We don't check for replaceable blocks. The user can deal with that. <_<
			
			y += 2; //Increase y by 2 to place the rift at head level
			if (!player.canPlayerEdit(x, y, z, 0, stack))
			{
				return false;
			}
			
			Point4D source = getSource(stack);
			if (source != null)
			{
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
					sourceWorld.setBlock(source.getX(), source.getY(), source.getY(), mod_pocketDim.blockRift.blockID);
				}
				
				if (!player.capabilities.isCreativeMode)
				{
					stack.stackSize--;
				}
				clearSource(stack);
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
		return false;
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		Point4D source = getSource(par1ItemStack);
		if (source != null)
		{
			par3List.add("Leads to (" + source.getX() + ", " + source.getY() + ", " + source.getZ() + ") at dimension #" + source.getDimension());
		}
		else
		{
			par3List.add("First click stores a location;");
			par3List.add("second click creates a pair of");
			par3List.add("rifts linking the two locations.");
		}
	}

	public static void setSource(ItemStack itemStack, int x, int y, int z, NewDimData dimension)
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger("linkX", x);
		tag.setInteger("linkY", y);
		tag.setInteger("linkZ", z);
		tag.setInteger("linkDimID", dimension.id());

		itemStack.setTagCompound(tag);
		itemStack.setItemDamage(1);
	}
	
	public static void clearSource(ItemStack itemStack)
	{
		//Don't just set the tag to null since there may be other data there (e.g. for renamed items)
		NBTTagCompound tag = itemStack.getTagCompound();
		tag.removeTag("linkX");
		tag.removeTag("linkY");
		tag.removeTag("linkZ");
		tag.removeTag("linkDimID");
		itemStack.setItemDamage(0);
	}
	
	public static Point4D getSource(ItemStack itemStack)
	{
		if (itemStack.getItemDamage() != 0)
		{
			if (itemStack.hasTagCompound())
			{
				NBTTagCompound tag = itemStack.getTagCompound();
				
				Integer x = tag.getInteger("linkX");
				Integer y = tag.getInteger("linkY");
				Integer z = tag.getInteger("linkZ");
				Integer dimID = tag.getInteger("linkDimID");
				
				if (x != null && y != null && z != null && dimID != null)
				{
					return new Point4D(x, y, z, dimID);
				}
			}
			itemStack.setItemDamage(0);
		}
		return null;
	}
}
