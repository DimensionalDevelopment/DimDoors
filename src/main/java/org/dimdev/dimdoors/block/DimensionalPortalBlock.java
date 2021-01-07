package org.dimdev.dimdoors.block;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.util.TeleportUtil;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DimensionalPortalBlock extends Block implements RiftProvider<EntranceRiftBlockEntity> {
	public DimensionalPortalBlock(Settings settings) {
		super(settings);
	}

	@Override
	public EntranceRiftBlockEntity getRift(World world, BlockPos pos, BlockState state) {
		return (EntranceRiftBlockEntity) this.createBlockEntity(world);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new EntranceRiftBlockEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (world.isClient) {
			return;
		}
		if (ModDimensions.isLimboDimension(entity.getEntityWorld())) {
			TeleportUtil.teleport(entity, DimensionalDoorsInitializer.getWorld(World.OVERWORLD), entity.getPos().subtract(0, entity.getPos().y, 0).add(0, 384, 0), 0);
		}
		super.onEntityCollision(state, world, pos, entity);
	}
}
