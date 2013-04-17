package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class linkDimDoor extends dimDoor
{
	private Icon blockIconBottom;
	public linkDimDoor(int par1, Material material) {
		super(par1, material);
		// TODO Auto-generated constructor stub
	}

	
	@SideOnly(Side.CLIENT)

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
       if(par1IBlockAccess.getBlockId(par2, par3-1, par4)==this.blockID)
       {
    	   return this.blockIcon;
       }
       else
       {
    	   return this.blockIconBottom;
       }
    }
	public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_top");
        this.blockIconBottom = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_bottom");

    }

}