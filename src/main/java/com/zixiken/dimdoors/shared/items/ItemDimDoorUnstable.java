package com.zixiken.dimdoors.shared.items;

import java.util.List;

import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class ItemDimDoorUnstable extends ItemDoorBase {

    public static final String ID = "itemDimDoorChaos";

    public ItemDimDoorUnstable() {
        super(ModBlocks.blockDimDoorChaos, null);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.translateToLocal("info.chaosDoor"));
    }

    @Override
    protected BlockDimDoorBase getDoorBlock() {
        return ModBlocks.blockDimDoorChaos;
    }
}
