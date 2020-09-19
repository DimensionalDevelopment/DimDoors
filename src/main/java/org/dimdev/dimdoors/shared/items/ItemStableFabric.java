package org.dimdev.dimdoors.shared.items;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import org.dimdev.dimdoors.DimDoors;

public class ItemStableFabric extends Item{

    public static final String ID = "stable_fabric";

    public ItemStableFabric(){
        setTranslationKey(ID);
        setFull3D();
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setRegistryName(DimDoors.MODID, ID);
    }

}
