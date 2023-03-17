package org.dimdev.dimdoors.world.decay.processors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
	public DecayProcessor fromNbt(NbtCompound json) {
		block = Registries.BLOCK.get(Identifier.tryParse(json.getString("block")));
		entropy = json.getInt("entropy");
		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		DecayProcessor.super.toNbt(nbt);
		nbt.putString("block", Registries.BLOCK.getId(block).toString());
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
	public int process(World world, BlockPos pos, BlockState origin, BlockState target, FluidState targetFluid) {
		if(target.getBlock() instanceof DoorBlock) {
			BlockPos otherPos = target.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos.up();

			Direction facing = target.get(DoorBlock.FACING);

			if(target.get(DoorBlock.OPEN)) facing = target.get(DoorBlock.HINGE) == DoorHinge.RIGHT ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();

			BlockState newState = block.getDefaultState().with(TrapdoorBlock.OPEN, true).with(TrapdoorBlock.FACING, facing);

			world.setBlockState(pos, newState);
			world.setBlockState(otherPos, newState);

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
