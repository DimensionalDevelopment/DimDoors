package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemLinkSignature extends Item {
    public static final String ID = "itemLinkSignature";

    public ItemLinkSignature() {
        super();
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        translateAndAdd("info.riftSignature.unbound", list);
    }
}
