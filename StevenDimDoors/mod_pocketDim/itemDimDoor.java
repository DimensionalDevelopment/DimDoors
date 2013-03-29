package StevenDimDoors.mod_pocketDim;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class itemDimDoor extends ItemDoor
{
    private Material doorMaterial;

    public itemDimDoor(int par1, Material par2Material)
    {
    	  super(par1, par2Material);
          this.doorMaterial = par2Material;
          this.setCreativeTab(CreativeTabs.tabTransport);
    }
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());

    }
    
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
    	
    
    		par3List.add("Place on the block under a rift");
    		par3List.add ("to activate that rift,");
    		par3List.add("or place anywhere else");
    		par3List.add("to create a pocket dim");


    	
    }
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par7 != 1)
        {
            return false;
        }
        else
        {
            ++par5;
            Block var11;

           
            if(par1ItemStack.getItem() instanceof itemExitDoor )
            {
                var11 = mod_pocketDim.ExitDoor;
            }
            
            else if(par1ItemStack.getItem() instanceof ItemChaosDoor )
            {
                var11 = mod_pocketDim.chaosDoor;
            }
            else
            {
                var11 = mod_pocketDim.dimDoor;
            }
            
            
            

            if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack)&&!par3World.isRemote)
            {
                int var12 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;

                if (!var11.canPlaceBlockAt(par3World, par4, par5, par6)||!this.canPlace(par3World, par4, par5, par6, var12))
                {
                    return false;
                }
                else 
                {
                
                    placeDoorBlock(par3World, par4, par5, par6, var12, var11);

                   
                    --par1ItemStack.stackSize;
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
    }
    
    public boolean canPlace(World world,int i, int j, int k, int p)
    {
    	
    	return true;
         
    }

}