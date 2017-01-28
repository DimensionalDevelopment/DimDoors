package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.item.Item;

public class ItemWorldThread extends Item {

    public static final String ID = "itemWorldThread";

    public ItemWorldThread() {
        super();
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }
}
