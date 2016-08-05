package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.blocks.BaseDimDoor;
import com.zixiken.dimdoors.DimDoors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemUnstableDoor extends BaseItemDoor {
    public static final String ID = "itemChaosDoor";

    public ItemUnstableDoor() {
        super(DimDoors.unstableDoor, null);
        setUnlocalizedName(ID);
    }

	@Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    	tooltip.add(StatCollector.translateToLocal("info.chaosDoor"));
    }
    
    @Override
    protected BaseDimDoor getDoorBlock() {return DimDoors.unstableDoor;}
}