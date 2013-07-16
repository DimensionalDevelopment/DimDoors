package StevenDimDoors.mod_pocketDim.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class ItemBlockDimWall extends ItemBlock
{
	private final static String[] subNames = {"Fabric of Reality", "Ancient Fabric"};
	
    public ItemBlockDimWall(int par1)
    {
    	  super(par1);
          this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
          setHasSubtypes(true);
    }
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName().replace("tile.", ""));
    }
    
    @Override
	public int getMetadata (int damageValue) 
    {
		return damageValue;
	}
	
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return subNames[getItemDamageFromStack(par1ItemStack)];
    }  
}