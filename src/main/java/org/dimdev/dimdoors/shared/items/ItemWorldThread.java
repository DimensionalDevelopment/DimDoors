package org.dimdev.dimdoors.shared.items;

import net.minecraft.item.Item;
import org.dimdev.dimdoors.DimDoors;

public class ItemWorldThread extends Item{

    public static final String ID = "world_thread";

    public ItemWorldThread(){
        setTranslationKey(ID);
        setFull3D();
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setRegistryName(DimDoors.MODID, ID);
    }

}
