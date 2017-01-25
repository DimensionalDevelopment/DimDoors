package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.item.Item;

public class ItemStableFabric extends Item {

    public static final String ID = "itemStableFabric";

    public ItemStableFabric() {
        super();
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }
}
