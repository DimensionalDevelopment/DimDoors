package org.dimdev.dimdoors.block;

import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


// TODO: Make this placeable on rifts
public class DimensionalTrapdoorBlock extends TrapdoorBlock implements RiftProvider<EntranceRiftBlockEntity> {
	public DimensionalTrapdoorBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && state.get(TrapdoorBlock.OPEN)) {
			this.getRift(world, pos, state).teleport(entity);
		}
	}


	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new EntranceRiftBlockEntity();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		state = state.cycle(OPEN);
		world.setBlockState(pos, state, 2);

		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		this.playToggleSound(player, world, pos, state.get(OPEN));
		return ActionResult.SUCCESS;
	}

	@Override
	public EntranceRiftBlockEntity getRift(World world, BlockPos pos, BlockState state) {
		return (EntranceRiftBlockEntity) world.getBlockEntity(pos);
	}
}
