package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemWorldThread extends Item {

    public static final String ID = "world_thread";

    public ItemWorldThread() {
        super();
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }
}
