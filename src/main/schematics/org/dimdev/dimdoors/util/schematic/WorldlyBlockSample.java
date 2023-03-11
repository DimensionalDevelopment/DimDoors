package org.dimdev.dimdoors.util.schematic;

import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;

import org.dimdev.dimdoors.api.util.BlockPlacementType;

public class WorldlyBlockSample implements BlockGetter, LevelSimulatedRW {
	private final RelativeBlockSample relativeBlockSample;
	private final WorldGenLevel world;

	public WorldlyBlockSample(RelativeBlockSample relativeBlockSample, WorldGenLevel world) {
		this.relativeBlockSample = relativeBlockSample;
		this.world = world;
	}

	public void place(BlockPos origin, boolean biomes) {
		this.relativeBlockSample.place(origin, this.world, BlockPlacementType.SECTION_NO_UPDATE, biomes);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.relativeBlockSample.getBlockEntity(pos);
	}

	@Override
	public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos pos, BlockEntityType<T> type) {
		 return Optional.ofNullable(type.getBlockEntity(this, pos));
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.relativeBlockSample.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.relativeBlockSample.getFluidState(pos);
	}

	@Override
	public boolean setBlock(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
		return this.relativeBlockSample.setBlock(pos, state, flags, maxUpdateDepth);
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean move) {
		return this.relativeBlockSample.removeBlock(pos, move);
	}

	@Override
	public boolean destroyBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
		return this.relativeBlockSample.destroyBlock(pos, drop, breakingEntity, maxUpdateDepth);
	}

	@Override
	public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> state) {
		return state.test(this.getBlockState(pos));
	}

	@Override
	public boolean isFluidAtPosition(BlockPos pos, Predicate<FluidState> state) {
		throw new RuntimeException("Method Implementation missing!"); // FIXME
	}

	@Override
	public BlockPos getHeightmapPos(Heightmap.Types type, BlockPos blockPos) {
		throw new RuntimeException("Method Implementation missing!"); // FIXME
	}

	@Override
	public int getHeight() {
		return this.relativeBlockSample.getHeight();
	}

	@Override
	public int getMinBuildHeight() {
		return this.relativeBlockSample.getMinBuildHeight();
	}
}
