package org.dimdev.dimdoors.shared.items;

import net.minecraft.item.Item;
import org.dimdev.dimdoors.DimDoors;

public class ItemGeneric extends Item {

    public ItemGeneric(String ID) {
        this.setTranslationKey(ID);
        this.setRegistryName(DimDoors.getResource(ID));
        this.setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        this.setFull3D();
    }
}
