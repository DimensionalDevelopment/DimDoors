package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.blocks.BaseDimDoor;
import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemUnstableDoor extends BaseItemDoor {
    public static final String ID = "itemChaosDoor";

    public ItemUnstableDoor() {
        super(DimDoors.unstableDoor, null);
        setUnlocalizedName(ID);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
    	par3List.add(StatCollector.translateToLocal("info.chaosDoor"));
    }
    
    @Override
    protected BaseDimDoor getDoorBlock()
	{
		return (BaseDimDoor) DimDoors.unstableDoor;
	}
}