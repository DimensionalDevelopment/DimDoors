package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBlockDimWall extends ItemBlock
{
	private final static String[] subNames = {"Fabric of Reality", "Fabric of Reality Permanent"};
	
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
        return this.subNames[getItemDamageFromStack(par1ItemStack)];
    }  
}