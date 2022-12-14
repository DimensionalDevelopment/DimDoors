package org.dimdev.dimdoors.datagen;

import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.BlockDecayProcessor;

public class FluidDecayProcessor implements DecayProcessor {
	public static final String KEY = "fluid";

	protected Fluid fluid;

	protected int entropy;

	public FluidDecayProcessor() {}

	protected FluidDecayProcessor(Fluid fluid, int entropy) {
		this.fluid = fluid;
		this.entropy = entropy;
	}

	@Override
	public DecayProcessor fromNbt(NbtCompound json) {
		fluid = Registries.FLUID.get(Identifier.tryParse(json.getString("fluid")));
		entropy = json.getInt("entropy");
		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		DecayProcessor.super.toNbt(nbt);
		nbt.putString("block", Registries.FLUID.getId(fluid).toString());
		nbt.putInt("entropy", entropy);
		return nbt;
	}

	@Override
	public DecayProcessorType<? extends DecayProcessor> getType() {
		return DecayProcessorType.FLUID_PROCESSOR_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public int process(World world, BlockPos pos, BlockState origin, BlockState target, FluidState targetFluid) {
		BlockState newState = fluid.getDefaultState().getBlockState();
		world.setBlockState(pos, newState);
		return entropy;
	}

	private static <T extends Comparable<T>> FluidState transferProperty(FluidState from, FluidState to, Property<T> property) {
		return to.with(property, from.get(property));
	}

	public static FluidDecayProcessor.Builder builder() {
		return new FluidDecayProcessor.Builder();
	}

	public static class Builder {
		private Fluid fluid = Fluids.EMPTY;
		private int entropy;

		public FluidDecayProcessor.Builder fluid(Fluid fluid) {
			this.fluid = fluid;
			return this;
		}

		public FluidDecayProcessor.Builder entropy(int entropy) {
			this.entropy = entropy;
			return this;
		}

		public FluidDecayProcessor create() {
			return new FluidDecayProcessor(fluid, entropy);
		}
	}
}
