package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.item.ItemSword;

/**
 * Created by Jared Johnson on 1/20/2017.
 */
public class ItemRiftBlade extends ItemSword {
    public static final String ID = "itemRiftBlade";

    public ItemRiftBlade() {
        super(ToolMaterial.IRON);
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }
}
