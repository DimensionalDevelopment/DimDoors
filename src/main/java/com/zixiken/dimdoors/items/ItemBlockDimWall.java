package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockDimWall extends ItemBlock {
	private final static String[] subNames = {"", "Ancient" , "Altered"};
	
    public ItemBlockDimWall(Block block) {
        super(block);
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setMaxDamage(0);
        setHasSubtypes(true);
    }
    
    @Override
	public int getMetadata (int damageValue) 
    {
		return damageValue;
	}
	
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + subNames[this.getDamage(stack)];
    }  
}