package StevenDimDoors.mod_pocketDim;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class itemLinkSignature extends Item
{
    private Material doorMaterial;

    public itemLinkSignature(int par1, Material par2Material)
    {
    	 super(par1);
    	 this.setMaxStackSize(1);
    	// this.setTextureFile("/PocketBlockTextures.png");
         this.setCreativeTab(CreativeTabs.tabTransport);

      //   this.iconIndex=5;
         this.setMaxDamage(0);
         this.hasSubtypes=true;
    	 //TODO move to proxy
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack par1ItemStack)
    {
    	// adds effect if item has a link stored
    	int key=par1ItemStack.getItemDamage();
		LinkData linkData= dimHelper.instance.interDimLinkList.get(key);
    	if(linkData!=null)
    	{
    		return true;
    	}
       return false;
    }
    
    
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());

    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
    	int key;
    	LinkData linkData;
		int thisWorldID=par3World.provider.dimensionId;
		
		

    	
    	if(!par3World.isRemote)
    	{		
    		
			//par1ItemStack= par2EntityPlayer.getCurrentEquippedItem();
			
    		
    		
    		key=par1ItemStack.getItemDamage();
    		linkData = dimHelper.instance.interDimLinkList.get(key);
    		//System.out.println(key);
    		int offset = 2;
    		if(linkData!=null&&key!=0)
    		{
    		// checks to see if the item has a link stored, if so, it creates it
    			if(par3World.getBlockId(par4, par5, par6)==Block.snow.blockID)
    			{
    				offset = 1;
    			}
    				dimHelper.instance.createLink(par3World.provider.dimensionId, linkData.destDimID, par4, par5+offset, par6, linkData.destXCoord, linkData.destYCoord, linkData.destZCoord);		
    				dimHelper.instance.createLink(linkData.destDimID, par3World.provider.dimensionId, linkData.destXCoord, linkData.destYCoord, linkData.destZCoord,par4, par5+offset, par6);		

    				--par1ItemStack.stackSize;
	    			par2EntityPlayer.sendChatToPlayer("Rift Created");
	    			if(par2EntityPlayer.capabilities.isCreativeMode)
	    			{
		    			par2EntityPlayer.sendChatToPlayer("Rift Signature Cleared");

	            		par2EntityPlayer.inventory.mainInventory[par2EntityPlayer.inventory.currentItem] = new ItemStack(this, 1, 0);

	    			
    			}
    			/**
    			else
    			{
	    			par2EntityPlayer.sendChatToPlayer("Both ends of a single rift cannot exist in the same dimension.");

    			}
    			**/
    		}
    		else 
        	{
    			if(par3World.getBlockId(par4, par5, par6)==Block.snow.blockID)
    			{
    				offset = 1;
    			}
    			//otherwise, it creates the first half of the link. Next click will complete it. 
    			key= dimHelper.instance.createUniqueInterDimLinkKey();
        		linkData= new LinkData(par3World.provider.dimensionId,par4, par5+offset, par6);
        		

        		dimHelper.instance.interDimLinkList.put(key, linkData);
        		par1ItemStack.setItemDamage(key);
        		
        		PacketHandler.linkKeyPacket(linkData, key);
    			par2EntityPlayer.sendChatToPlayer("Rift Signature Stored");

        		par2EntityPlayer.inventory.mainInventory[par2EntityPlayer.inventory.currentItem] = new ItemStack(this, 1, key);

        	}
    		
    		//dimHelper.instance.save();
    	}
    	
    	
    	return true;
		
    
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
    	
    	LinkData linkData = dimHelper.instance.interDimLinkList.get(par1ItemStack.getItemDamage());
    	if(linkData!=null)
    	{
    		par3List.add(String.valueOf("Leads to dim "+linkData.destDimID +" at depth "+dimHelper.instance.getDimDepth(linkData.destDimID)));
    
    	}
    	else
    	{
    		par3List.add("First click stores location,");
    		par3List.add ("second click creates two rifts,");
    		par3List.add("that link the first location");
    		par3List.add("with the second location");


    	}
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
