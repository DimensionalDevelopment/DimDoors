package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.DimDoors;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemWorldThread extends Item {

    public static final String ID = "world_thread";

    public ItemWorldThread() {
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }
}
