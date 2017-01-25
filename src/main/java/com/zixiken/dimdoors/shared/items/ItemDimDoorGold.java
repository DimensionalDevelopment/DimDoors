package com.zixiken.dimdoors.shared.items;

import java.util.List;

import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemDimDoorGold extends ItemDoorBase {

    public static final String ID = "itemDimDoorGold";

    public ItemDimDoorGold() {
        super(ModBlocks.blockDimDoorGold, ModItems.itemDoorGold);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        translateAndAdd("info.goldDimDoor", tooltip);
    }

    @Override
    protected BlockDimDoorBase getDoorBlock() {
        return ModBlocks.blockDimDoorGold;
    }
}
