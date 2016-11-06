package com.zixiken.dimdoors.items;

import java.util.List;

import com.zixiken.dimdoors.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemDimDoorUnstable extends ItemDoorBase {
    public static final String ID = "itemDimDoorChaos";

    public ItemDimDoorUnstable() {
        super(ModBlocks.blockDimDoorChaos, null);
        setUnlocalizedName(ID);
    }

	@Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    	//tooltip.add(StatCollector.translateToLocal("info.chaosDoor"));
    }
    
    @Override
    protected BlockDimDoorBase getDoorBlock() {return ModBlocks.blockDimDoorChaos;}
}