package org.dimdev.dimdoors.world.decay.processors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.dimdev.dimdoors.world.decay.DecayProcessor;

public class DoorDecayProccessor implements DecayProcessor {
	public static final String KEY = "door";

	protected Block block;

	protected int entropy;

	public DoorDecayProccessor() {}

	protected DoorDecayProccessor(Block block, int entropy) {
		this.block = block;
		this.entropy = entropy;
	}

	@Override
	public DecayProcessor fromNbt(CompoundTag json) {
		block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(json.getString("block")));
		entropy = json.getInt("entropy");
		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		DecayProcessor.super.toNbt(nbt);
		nbt.putString("block", BuiltInRegistries.BLOCK.getKey(block).toString());
		nbt.putInt("entropy", entropy);
		return nbt;
	}

	@Override
	public DecayProcessorType<? extends DecayProcessor> getType() {
		return DecayProcessorType.DOOR_PROCESSOR_TYPE;
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public int process(Level world, BlockPos pos, BlockState origin, BlockState target) {
		if(target.getBlock() instanceof DoorBlock) {
			BlockPos otherPos = target.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();

			Direction facing = target.getValue(DoorBlock.FACING);

			if(target.getValue(DoorBlock.OPEN)) facing = target.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT ? facing.getCounterClockWise() : facing.getClockWise();

			BlockState newState = block.defaultBlockState().setValue(TrapDoorBlock.OPEN, true).setValue(TrapDoorBlock.FACING, facing);

			world.setBlockAndUpdate(pos, newState);
			world.setBlockAndUpdate(otherPos, newState);

			return entropy;
		}

		return 0;
	}

	public static DoorDecayProccessor.Builder builder() {
		return new DoorDecayProccessor.Builder();
	}

	public static class Builder {
		private Block block = Blocks.AIR;
		private int entropy;

		public Builder block(Block block) {
			this.block = block;
			return this;
		}

		public Builder entropy(int entropy) {
			this.entropy = entropy;
			return this;
		}

		public DoorDecayProccessor create() {
			return new DoorDecayProccessor(block, entropy);
		}
	}
}
