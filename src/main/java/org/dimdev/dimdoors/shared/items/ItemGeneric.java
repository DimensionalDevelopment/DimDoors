package org.dimdev.dimdoors.shared.items;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.dimdev.dimdoors.DimDoors;

public class ItemGeneric extends Item {

    public ItemGeneric(String ID) {
        this.setUnlocalizedName(ID);
        this.setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        this.setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        this.setFull3D();
    }
}
