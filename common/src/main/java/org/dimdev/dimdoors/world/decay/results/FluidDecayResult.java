package org.dimdev.dimdoors.world.decay.results;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.dimdev.dimdoors.world.decay.DecayResult;
import org.dimdev.dimdoors.world.decay.DecayResultType;

public class FluidDecayResult implements DecayResult {
	public static final String KEY = "fluid";

	protected Fluid fluid;

	protected int entropy;

	public FluidDecayResult() {
	}

	protected FluidDecayResult(Fluid fluid, int entropy) {
		this.fluid = fluid;
		this.entropy = entropy;
	}

	@Override
	public FluidDecayResult fromNbt(CompoundTag json) {
		fluid = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(json.getString("fluid")));
		entropy = json.getInt("entropy");
		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		DecayResult.super.toNbt(nbt);
		nbt.putString("block", BuiltInRegistries.FLUID.getKey(fluid).toString());
		nbt.putInt("entropy", entropy);
		return nbt;
	}

	@Override
	public DecayResultType<FluidDecayResult> getType() {
		return DecayResultType.FLUID_PROCESSOR_TYPE.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public int process(Level world, BlockPos pos, BlockState origin, BlockState target, FluidState targetFluid) {
		BlockState newState = fluid.defaultFluidState().createLegacyBlock();
		world.setBlockAndUpdate(pos, newState);
		return entropy;
	}

	@Override
	public Object produces(Object prior) {
		return FluidStack.create(fluid, 1000);
	}

	private static <T extends Comparable<T>> FluidState transferProperty(FluidState from, FluidState to, Property<T> property) {
		return to.setValue(property, from.getValue(property));
	}

	public static FluidDecayResult.Builder builder() {
		return new FluidDecayResult.Builder();
	}

	public static class Builder {
		private Fluid fluid = Fluids.EMPTY;
		private int entropy;

		public FluidDecayResult.Builder fluid(Fluid fluid) {
			this.fluid = fluid;
			return this;
		}

		public FluidDecayResult.Builder entropy(int entropy) {
			this.entropy = entropy;
			return this;
		}

		public FluidDecayResult create() {
			return new FluidDecayResult(fluid, entropy);
		}
	}
}