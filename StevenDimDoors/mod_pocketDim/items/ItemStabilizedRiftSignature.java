package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.NewLinkData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStabilizedRiftSignature extends itemLinkSignature
{
	private static DDProperties properties = null;
	
    public ItemStabilizedRiftSignature(int par)
    {
    	 super(par);
    	 this.setMaxStackSize(1);
         this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
         this.setMaxDamage(0);
         this.hasSubtypes=true;         
         if (properties == null)
        	 properties = DDProperties.instance();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack par1ItemStack)
    {
    	// adds effect if item has a link stored
    	if(par1ItemStack.hasTagCompound())
    	{
    		if(par1ItemStack.stackTagCompound.getBoolean("isCreated"))
    		{
    		return true;
    		}
    	}
    	return false;
    }
    
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
    	int key;
    	NewLinkData linkData;
		int thisWorldID=par3World.provider.dimensionId;
		Integer[] linkCoords =this.readFromNBT(par1ItemStack);

    	int offset = 2;
    	if(par1ItemStack.getTagCompound()!=null)
    	{
    		if(par1ItemStack.getTagCompound().getBoolean("isCreated"))
    		{
    			boolean hasEnder = false;
    			// checks to see if the item has a link stored, if so, it creates it	
    			if(par2EntityPlayer.inventory.hasItem(Item.enderPearl.itemID)||par2EntityPlayer.inventory.hasItem(properties.StableFabricItemID))
    			{
    				if(!par2EntityPlayer.inventory.consumeInventoryItem(properties.StableFabricItemID))
    				{
        				par2EntityPlayer.inventory.consumeInventoryItem(Item.enderPearl.itemID);
    				}
    				hasEnder=true;
    			} 				
    			if(par3World.getBlockId(par4, par5, par6)==Block.snow.blockID)
    			{
    				offset = 1;
    			}
    			if(hasEnder&&!par3World.isRemote)
    			{
    				if(PocketManager.instance.getLinkDataFromCoords(linkCoords[0], linkCoords[1], linkCoords[2], par3World)==null)
    				{
        				PocketManager.instance.createLink(linkCoords[3], par3World.provider.dimensionId, linkCoords[0], linkCoords[1], linkCoords[2],par4, par5+offset, par6);	
    				}
    				PocketManager.instance.createLink(par3World.provider.dimensionId, linkCoords[3], par4, par5+offset, par6, linkCoords[0], linkCoords[1], linkCoords[2]);	
					par2EntityPlayer.worldObj.playSoundAtEntity(par2EntityPlayer,"mods.DimDoors.sfx.riftEnd", (float) .6, 1);

    				par2EntityPlayer.sendChatToPlayer("Rift Created");
    			}
    			else if(!par3World.isRemote)
    			{
    				par2EntityPlayer.sendChatToPlayer("No Ender Pearls!");
    			}
    		}
    	}
    	else if(!par3World.isRemote)
        {
    		if(par3World.getBlockId(par4, par5, par6)==Block.snow.blockID)
    		{
    			offset = 1;
    		}
    		//otherwise, it creates the first half of the link. Next click will complete it. 
    		key= PocketManager.instance.createUniqueInterDimLinkKey();
        	this.writeToNBT(par1ItemStack, par4, par5+offset, par6,par3World.provider.dimensionId);
			par2EntityPlayer.worldObj.playSoundAtEntity(par2EntityPlayer,"mods.DimDoors.sfx.riftStart", (float) .6, 1);

    		par2EntityPlayer.sendChatToPlayer("Rift Signature Stored");
        }
    	return true;	
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
    	if(par1ItemStack.hasTagCompound())
    	{
    		if(par1ItemStack.stackTagCompound.getBoolean("isCreated"))
    		{
    			Integer[] coords = this.readFromNBT(par1ItemStack);
    			par3List.add(String.valueOf("Leads to dim "+coords[3] +" with depth "+PocketManager.instance.getDimDepth(PocketManager.instance.getDimDepth(coords[3]))));
        		par3List.add("at x="+coords[0]+" y="+coords[1]+" z="+coords[2]);
    		}
    	}
    	else
    	{
    		par3List.add("First click stores location,");
    		par3List.add ("second click creates two rifts,");
    		par3List.add("that link the first location");
    		par3List.add("with the second location");
    	}
    }
    
    public void writeToNBT(ItemStack itemStack,int x, int y, int z, int dimID)
    {
    	NBTTagCompound tag;

    	if(itemStack.hasTagCompound())
    	{
    		tag = itemStack.getTagCompound();  	   
    	}
    	else
    	{
    		tag= new NBTTagCompound();
    	}  
    	tag.setInteger("linkX", x);
    	tag.setInteger("linkY", y);
    	tag.setInteger("linkZ", z);
    	tag.setInteger("linkDimID", dimID);
    	tag.setBoolean("isCreated", true);
    	itemStack.setTagCompound(tag);
    }

    /**
     * Read the stack fields from a NBT object.
     */
    public Integer[] readFromNBT(ItemStack itemStack)
    {	
    	NBTTagCompound tag;
    	Integer[] linkCoords = new Integer[5];
    	if(itemStack.hasTagCompound())
    	{
    		tag = itemStack.getTagCompound();

    		if(!tag.getBoolean("isCreated"))
    		{
    			return null;
    		}
    		linkCoords[0]=tag.getInteger("linkX");
    		linkCoords[1]=tag.getInteger("linkY");
    		linkCoords[2]=tag.getInteger("linkZ");
    		linkCoords[3]=tag.getInteger("linkDimID");
        }
    	return linkCoords;    
    }
    
    
    @Override
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) 
    {
    	if(!par2World.isRemote)
    	{
    		/**
    		//creates the first half of the link on item creation
    		int key= dimHelper.instance.createUniqueInterDimLinkKey();
    		LinkData linkData= new LinkData(par2World.provider.dimensionId,MathHelper.floor_double(par3EntityPlayer.posX),MathHelper.floor_double(par3EntityPlayer.posY),MathHelper.floor_double(par3EntityPlayer.posZ));
    		System.out.println(key);

    		dimHelper.instance.interDimLinkList.put(key, linkData);
    		par1ItemStack.setItemDamage(key);
    		**/
    	}
    }
}
