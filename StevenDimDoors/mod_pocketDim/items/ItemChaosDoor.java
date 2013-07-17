package StevenDimDoors.mod_pocketDim.items;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class ItemChaosDoor extends itemDimDoor
{
    public ItemChaosDoor(int par1, Material par2Material)
    {
    	  super(par1, par2Material);
          this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
    }

    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());

    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
    	par3List.add("Caution: Leads to random destination");
    }
    
}