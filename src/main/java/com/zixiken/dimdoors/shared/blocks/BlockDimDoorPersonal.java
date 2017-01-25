package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.shared.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class BlockDimDoorPersonal extends BlockDimDoorBase {

    public static final String ID = "blockDimDoorPersonal";

    public BlockDimDoorPersonal() {
        super(Material.ROCK);
        setHardness(0.1F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public Item getItemDoor() {
        return ModItems.itemDimDoorPersonal;
    }

}
