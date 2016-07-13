package com.zixiken.dimdoors.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.world.LimboDecay;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockLimbo extends Block {
	public static final String ID = "blockLimbo";

	private final int limboDimensionID;
	private final LimboDecay decay;
	
	public BlockLimbo(LimboDecay decay) {
		super(Material.ground);
		limboDimensionID = DDProperties.instance().LimboDimensionID;
		this.decay = decay;
		setTickRandomly(true);
		setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setHardness(.2F);
        setUnlocalizedName(ID);
        setLightLevel(.0F);
	}
	
	/**
	 * If the block is in Limbo, attempt to decay surrounding blocks upon receiving a random update tick.
	 */
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(worldIn.provider.getDimensionId() == limboDimensionID) decay.applySpreadDecay(worldIn, pos);
	}
}
