package org.dimdev.dimdoors.world.decay.processors;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.dimdev.dimdoors.world.decay.DecayProcessor;

public class DoubleDecayProcessor implements DecayProcessor {
	public static final String KEY = "double";

	protected Block block;

	protected int entropy;

	public DoubleDecayProcessor() {}

	protected DoubleDecayProcessor(Block block, int entropy) {
		this.block = block;
		this.entropy = entropy;
	}

	@Override
	public DecayProcessor fromNbt(NbtCompound json) {
		block = Registry.BLOCK.get(Identifier.tryParse(json.getString("block")));
		entropy = json.getInt("entropy");
		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		DecayProcessor.super.toNbt(nbt);
		nbt.putString("block", Registry.BLOCK.getId(block).toString());
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
	public int process(World world, BlockPos pos, BlockState origin, BlockState target) {
		if(target.getBlock() instanceof DoorBlock) {
			BlockPos otherPos = target.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos.up();

			Direction facing = target.get(DoorBlock.FACING);

			if(target.get(DoorBlock.OPEN)) facing = target.get(DoorBlock.HINGE) == DoorHinge.RIGHT ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();

			BlockState newState = block.getDefaultState().with(TrapdoorBlock.OPEN, true).with(TrapdoorBlock.FACING, facing);

			world.setBlockState(pos, newState);
			world.setBlockState(otherPos, newState);

			return entropy;
		} else if(target.getBlock() instanceof BedBlock) {
			BlockPos otherPos = pos.offset(BedBlock.getOppositePartDirection(target));

		}

		return 0;
	}

	public static DoubleDecayProcessor.Builder builder() {
		return new DoubleDecayProcessor.Builder();
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

		public DoubleDecayProcessor create() {
			return new DoubleDecayProcessor(block, entropy);
		}
	}
}
