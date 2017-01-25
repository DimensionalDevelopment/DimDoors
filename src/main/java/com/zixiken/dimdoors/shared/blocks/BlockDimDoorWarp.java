package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.shared.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class BlockDimDoorWarp extends BlockDimDoorBase {

    public static final String ID = "blockDimDoorWarp";

    public BlockDimDoorWarp() {
        super(Material.WOOD);
        setHardness(1.0F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public Item getItemDoor() {
        return ModItems.itemDimDoorWarp;
    }
}
