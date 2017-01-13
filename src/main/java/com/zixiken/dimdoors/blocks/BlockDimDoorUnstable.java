package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

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
    public void placeLink(World world, BlockPos pos) {
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
