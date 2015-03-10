package StevenDimDoors.mod_pocketDim.items;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class ItemBlockDimWall extends ItemBlock
{
	private final static String[] subNames = {"tile.blockDimWall", "tile.blockAncientWall" , "tile.blockAlteredWall"};
	
    public ItemBlockDimWall(Block block)
    {
    	  super(block);
          this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
          setHasSubtypes(true);
    }
    @Override
	public void registerIcons(IIconRegister par1IconRegister)
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
        return subNames[this.getDamage(par1ItemStack)];
    }  
}