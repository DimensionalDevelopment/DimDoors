package org.dimdev.dimdoors.block;

import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface RiftProvider<T extends RiftBlockEntity> extends BlockEntityProvider {
	T getRift(World world, BlockPos pos, BlockState state);

	@Environment(EnvType.CLIENT)
	default boolean isTall(BlockState cachedState) {
		return false;
	}
}
