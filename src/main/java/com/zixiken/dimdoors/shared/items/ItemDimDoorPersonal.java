package com.zixiken.dimdoors.shared.items;

import java.util.List;

import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemDimDoorPersonal extends ItemDoorBase {

    public static final String ID = "itemDimDoorQuartz";

    public ItemDimDoorPersonal() {
        super(ModBlocks.blockDimDoorPersonal, ModItems.itemDoorQuartz);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        translateAndAdd("info.personalDimDoor", tooltip);
    }

    @Override
    protected BlockDimDoorBase getDoorBlock() {
        return ModBlocks.blockDimDoorPersonal;
    }
}
