package org.dimdev.dimdoors.util.schematic;

import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.world.Heightmap;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.StructureWorldAccess;

public class WorldlyBlockSample implements BlockView, ModifiableTestableWorld {
	private final RelativeBlockSample relativeBlockSample;
	private final StructureWorldAccess world;

	public WorldlyBlockSample(RelativeBlockSample relativeBlockSample, StructureWorldAccess world) {
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
		 return Optional.ofNullable(type.get(this, pos));
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
	public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
		return this.relativeBlockSample.setBlockState(pos, state, flags, maxUpdateDepth);
	}

	@Override
	public boolean removeBlock(BlockPos pos, boolean move) {
		return this.relativeBlockSample.removeBlock(pos, move);
	}

	@Override
	public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
		return this.relativeBlockSample.breakBlock(pos, drop, breakingEntity, maxUpdateDepth);
	}

	@Override
	public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
		return state.test(this.getBlockState(pos));
	}

	@Override
	public boolean testFluidState(BlockPos pos, Predicate<FluidState> state) {
		throw new RuntimeException("Method Implementation missing!"); // FIXME
	}

	@Override
	public BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
		throw new RuntimeException("Method Implementation missing!"); // FIXME
	}

	@Override
	public int getHeight() {
		return this.relativeBlockSample.getHeight();
	}

	@Override
	public int getBottomY() {
		return this.relativeBlockSample.getBottomY();
	}
}
