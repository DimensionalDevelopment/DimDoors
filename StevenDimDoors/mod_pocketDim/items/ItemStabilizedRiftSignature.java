package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	//Check if the Stabilized Rift Signature has been initialized
    	Point4D source = getSource(stack);
    	if (source != null)
    	{
    		//Yes, it's initialized. Check if the player can pay an Ender Pearl to create a rift.
    		if (player.inventory.hasItem(Item.enderPearl.itemID))
    		{
	    		if (tryItemUse(stack, player, world, x, y, z) && !player.capabilities.isCreativeMode)
	    		{
	    			player.inventory.consumeInventoryItem(Item.enderPearl.itemID);
	    		}
    		}
    	}
    	else
    	{
    		//Initialization doesn't cost any materials
    		tryItemUse(stack, player, world, x, y, z);
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
