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
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class ItemDDKey extends Item
{
	public ItemDDKey(int itemID)
	{
		super(itemID);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
		this.setMaxStackSize(1);

	}

    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) 
    {
    	boolean check = (this.isBound(par1ItemStack) ? par3List.add("Bound") : par3List.add("Unbound"));
    	return;
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
		return !this.isBound(par1ItemStack);
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
		//make sure we are not trying to mess with a door thats already locked by someone else
		if(!this.canKeyOpen(link, itemStack)&&link.isLocked())
		{
			return false;
		}
		
		//see if we can bind this key to this door and lock it
		if(setBoundDoor(itemStack, link))
		{
			link.setLocked(true);
			return false;
		}
		
		//lastly, just see if we can toggle the door's lock state if its locked.
		if(this.canKeyOpen(link, itemStack))
		{
			link.setLocked(!link.isLocked());
			return false;
		}
		
		return false;
	}

	
	public boolean setBoundDoor(ItemStack itemStack, DimLink link)
	{
		//dont bind to a door if we already are bound, or if we dont have permission to lock that door
		if(this.isBound(itemStack)|| (!this.canKeyOpen(link, itemStack)&&link.isLocked()))
		{
			return false;
		}
		
		//dont bind if the door has a lock already on it, but we can still open it. That would waste the key.
		if(link.isLocked())
		{
			return false;
		}
		
		//init tags
		if(!itemStack.hasTagCompound())
		{
			this.initNBTTags(itemStack);
		}
		
		//consume this keys ability to create a lock
		itemStack.getTagCompound().setBoolean("HasLockedDoor", true);
		
		//create the tag that binds this door to this key
		NBTTagCompound tag = new NBTTagCompound();

		int x = link.source().getX();
		int y = link.source().getY();
		int z = link.source().getZ();

		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
		tag.setInteger("dim", link.source().getDimension());

		//add this door's tag to this keys keyring
		NBTTagList keyRing = itemStack.getTagCompound().getTagList("DDKeys");
		keyRing.appendTag(tag);
		itemStack.getTagCompound().setTag("DDKeys", keyRing);
		
		return true;
	}
	
	/**
	 * copies all the tags from the first key onto the second key
	 * @param givingKey
	 * @param receivingKey
	 */
	public void addDoorToKey(ItemStack givingKey, ItemStack receivingKey)
	{
		//cant copy tags from a key with no tags
		if(!givingKey.hasTagCompound())
		{
			return;
		}
		
		//initialize the receiving key
		if(!receivingKey.hasTagCompound())
		{
			this.initNBTTags(receivingKey);
		}
		
		//get the tags
		NBTTagCompound recevingTags = receivingKey.getTagCompound();
		NBTTagCompound sendingTags = (NBTTagCompound) givingKey.getTagCompound().copy();
		
		//copy over the actual tags		
		for(int i = 0; i<sendingTags.getTagList("DDKeys").tagCount();i++)
		{
			recevingTags.getTagList("DDKeys").appendTag(sendingTags.getTagList("DDKeys").tagAt(i));
		}
		
	}

	/**
	 * see if we could unlock this door if it where locked.
	 * @param link
	 * @param itemStack
	 * @return
	 */
	public boolean canKeyOpen(DimLink link, ItemStack itemStack)
	{
		//make sure we have a tag
		if (itemStack.hasTagCompound())
		{
			NBTTagList tags = itemStack.getTagCompound().getTagList("DDKeys");
			
			for(int i = 0; i < tags.tagCount(); i++)
			{
				NBTTagCompound tag = (NBTTagCompound) tags.tagAt(i);
				Integer x = tag.getInteger("x");
				Integer y = tag.getInteger("y");
				Integer z = tag.getInteger("z");
				Integer dimID = tag.getInteger("dim");
				if (x != null && y != null && z != null && dimID != null)
				{
					if (x == link.source().getX() && 
							y == link.source().getY() &&
							z == link.source().getZ() && 
							dimID == link.source().getDimension())
					{
						return true;
					}
				}
				
			}
		}
		return false;
	}
	

	public String getItemStackDisplayName(ItemStack par1ItemStack)
	{
		return StatCollector.translateToLocal(this.getUnlocalizedName(par1ItemStack) + ".name");
	}
	
	public boolean isBound(ItemStack item)
	{
		if(item.hasTagCompound())
    	{
    		if(item.getTagCompound().getBoolean("HasLockedDoor"))
    		{
    			return true;
    		}
    	}
		return false;
	}
	public void initNBTTags(ItemStack itemStack)
	{
		itemStack.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = itemStack.getTagCompound();
		tag.setTag("DDKeys", new NBTTagList());
		itemStack.setTagCompound(tag);
	}
}
