package org.dimdev.dimdoors.block;

import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface RiftProvider<T extends RiftBlockEntity> extends BlockEntityProvider {
	T getRift(World world, BlockPos pos, BlockState state);

	default boolean isTall(BlockState cachedState) {
		return false;
	}
}
