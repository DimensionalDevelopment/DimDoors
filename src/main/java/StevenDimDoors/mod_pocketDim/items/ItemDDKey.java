package StevenDimDoors.mod_pocketDim.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.blocks.IDimDoor;
import StevenDimDoors.mod_pocketDim.core.DDLock;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;

public class ItemDDKey extends Item
{
	public ItemDDKey(int itemID)
	{
		super(itemID);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		this.setMaxStackSize(1);

	}
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
    	
    }

    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) 
    {
    	if(DDLock.hasCreatedLock(par1ItemStack))
   		{
    		par3List.add("Bound");
   		}
    	else
    	{
    		par3List.add("Unbound");
    	}
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
		return !DDLock.hasCreatedLock(par1ItemStack);
	}
	
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		return false;
	}
	  
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float playerX, float playerY,
			float playerZ)
	{
		if(world.isRemote)
		{
			return false;
		}
		int blockID = world.getBlockId(x, y, z);
		//make sure we are dealing with a door
		if (!(Block.blocksList[blockID] instanceof IDimDoor))
		{
			return false;
		}
		
		DimLink link = PocketManager.getLink(x, y, z, world);
		//dont do anything to doors without links
		if (link == null)
		{
			return false;
		}

		//what to do if the door has a lock already
		if(link.hasLock())
		{
			if(link.canOpen(itemStack))
			{
				if(link.isLocked())
				{
					world.playSoundAtEntity(player, mod_pocketDim.modid + ":keyUnlock",  1F, 1F);
				}
				else
				{
					world.playSoundAtEntity(player, mod_pocketDim.modid + ":keyLock",  1F, 1F);
				}
				link.getLock().lock(!link.isLocked());
				PocketManager.getLinkWatcher().update(new ClientLinkData(link.source(),link.getLock()));
			}
			else
			{
				world.playSoundAtEntity(player, mod_pocketDim.modid + ":doorLocked",  1F, 1F);
			}
		}
		else
		{
			if(!DDLock.hasCreatedLock(itemStack))
			{
				world.playSoundAtEntity(player, mod_pocketDim.modid + ":keyLock",  1F, 1F);
				link.createLock(itemStack, world.rand.nextInt(Integer.MAX_VALUE));
				PocketManager.getLinkWatcher().update(new ClientLinkData(link.source(),link.getLock()));
			}
		}
		return false;
	}


	public String getItemStackDisplayName(ItemStack par1ItemStack)
	{
		return StatCollector.translateToLocal(this.getUnlocalizedName(par1ItemStack) + ".name");
	}
}
