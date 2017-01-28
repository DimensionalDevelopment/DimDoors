package com.zixiken.dimdoors.shared.items;

import java.util.List;

import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemDimDoorWarp extends ItemDoorBase {

    public static final String ID = "itemDimDoorWarp";

    public ItemDimDoorWarp() {
        super(ModBlocks.blockDimDoorWarp, (ItemDoor) Items.OAK_DOOR);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        translateAndAdd("info.warpDoor", tooltip);
    }

    @Override
    protected BlockDimDoorBase getDoorBlock() {
        return ModBlocks.blockDimDoorWarp;
    }
}
