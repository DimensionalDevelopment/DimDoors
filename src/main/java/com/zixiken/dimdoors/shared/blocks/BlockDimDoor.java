package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.shared.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class BlockDimDoor extends BlockDimDoorBase {

    public static final String ID = "blockDimDoor";

    public BlockDimDoor() {
        super(Material.IRON);
        setHardness(1.0F);
        setResistance(2000.0F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public Item getItemDoor() {
        return ModItems.itemDimDoor;
    }
}
