package StevenDimDoors.mod_pocketDim.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class ItemDDKey extends Item
{
	public ItemDDKey(int itemID)
	{
		super(itemID);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
	}

	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack)
	{
		return true;
	}

	public static void setBoundDoor(ItemStack itemStack, DimLink link)
	{
		NBTTagCompound tag = new NBTTagCompound();

		int x = link.source().getX();
		int y = link.source().getY();
		int z = link.source().getZ();

		tag.setInteger("linkX", x);
		tag.setInteger("linkY", y);
		tag.setInteger("linkZ", z);
		tag.setInteger("linkDimID", link.source().getDimension());

		itemStack.setTagCompound(tag);
		itemStack.setItemDamage(1);
	}

	public static DimLink getBoundLink(ItemStack itemStack)
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
				return PocketManager.getLink(x, y, z, dimID);
			}
		}
		return null;
	}
	

}
