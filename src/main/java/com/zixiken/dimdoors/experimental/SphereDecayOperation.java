package com.zixiken.dimdoors.experimental;

import java.util.Random;

import com.zixiken.dimdoors.helpers.BlockPosHelper;
import com.zixiken.dimdoors.schematic.WorldOperation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Provides an operation for damaging structures based on a spherical area. The chance of damage decreases
 * with the square of the distance from the center of the sphere.
 * @author SenseiKiwi
 *
 */
public class SphereDecayOperation extends WorldOperation
{
	private Random random;
	private double scaling;
	private BlockPos center;
	private IBlockState primaryBlock;
	private IBlockState secondaryBlock;

	public SphereDecayOperation(Random random, IBlockState primaryBlock, IBlockState secondaryBlock) {
		super("SphereDecayOperation");
		this.random = random;
		this.primaryBlock = primaryBlock;
		this.secondaryBlock = secondaryBlock;
	}
	
	@Override
	protected boolean initialize(World world, BlockPos pos, BlockPos volume) {
		// Calculate a scaling factor so that the probability of decay
		// at the edge of the largest dimension of our bounds is 20%.
		scaling = Math.max(pos.getX() - 1, Math.max(pos.getY() - 1, pos.getZ() - 1)) / 2.0;
		scaling *= scaling * 0.20;

		center = pos.add(BlockPosHelper.divide(volume, 2.0));
		return true;
	}

	@Override
	protected boolean applyToBlock(World world, BlockPos pos) {
		// Don't raise any notifications. This operation is only designed to run
		// when a dimension is being generated, which means there are no players around.
		if (!world.isAirBlock(pos)) {
			double squareDistance = center.distanceSq(pos);
			
			if (squareDistance < 0.5 || random.nextDouble() < scaling / squareDistance) {
				world.setBlockState(pos, primaryBlock);
			} else if (random.nextDouble() < scaling / squareDistance) {
				world.setBlockState(pos, secondaryBlock);
			}
		}
		return true;
	}
}
