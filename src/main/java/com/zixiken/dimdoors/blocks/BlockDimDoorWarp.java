package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.items.ModItems;
import com.zixiken.dimdoors.shared.Location;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDimDoorWarp extends BlockDimDoorBase {

    public static final String ID = "blockDimDoorWarp";

    public BlockDimDoorWarp() {
        super(Material.WOOD);
        setHardness(1.0F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void placeLink(Location location) {
    }

    @Override
    public Item getItemDoor() {
        return ModItems.itemDimDoorWarp;
    }
}
