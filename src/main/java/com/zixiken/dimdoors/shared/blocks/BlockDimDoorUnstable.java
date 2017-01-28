package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.shared.items.ModItems;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockDimDoorUnstable extends BlockDimDoorBase {

    public static final String ID = "blockDimDoorChaos";

    public BlockDimDoorUnstable() {
        super(Material.IRON);
        setHardness(.2F);
        setUnlocalizedName(ID);
        setRegistryName(ID);
        setLightLevel(.0F);
    }

    @Override
    public Item getItemDoor() {
        return ModItems.itemDimDoorChaos;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune) {
        return Items.IRON_DOOR;
    }
}
