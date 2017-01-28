package com.zixiken.dimdoors.shared.blocks;

import com.zixiken.dimdoors.shared.world.limbo.LimboDecay;
import com.zixiken.dimdoors.shared.world.limbo.WorldProviderLimbo;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockLimbo extends Block {
    public static final String ID = "blockLimbo";

    public BlockLimbo() {
        super(Material.GROUND, MapColor.BLACK);
        setUnlocalizedName(ID);
        setRegistryName(ID);

        setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        //Make sure this block is in Limbo
        if (world.provider instanceof WorldProviderLimbo) {
            LimboDecay.applySpreadDecay(world, pos);
        }
    }
}
