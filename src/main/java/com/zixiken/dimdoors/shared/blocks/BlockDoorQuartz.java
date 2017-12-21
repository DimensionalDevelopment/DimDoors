package com.zixiken.dimdoors.shared.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.items.ModItems;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDoorQuartz extends BlockDoor {

    public static final String ID = "quartz_door";

    public BlockDoorQuartz() {
        super(Material.ROCK);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setUnlocalizedName(ID);
        setCreativeTab(CreativeTabs.REDSTONE);
        setHardness(0.1F);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : ModItems.QUARTZ_DOOR;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(ModItems.QUARTZ_DOOR);
    }
}
