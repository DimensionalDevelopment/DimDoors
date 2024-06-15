package org.dimdev.dimdoors.world.decay.conditions;

import com.google.common.collect.Streams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayCondition;

import java.util.Set;
import java.util.stream.Collectors;

public class FluidDecayCondition implements DecayCondition {
	public static final String KEY = "fluid";

	private Fluid fluid;
	private TagKey<Fluid> tag;

	public FluidDecayCondition() {}

	public FluidDecayCondition(TagKey<Fluid> tag, Fluid fluid) {
		this.tag = tag;
		this.fluid = fluid;
	}

	@Override
	public DecayCondition fromNbt(CompoundTag nbt) {
		String name = nbt.getString("entry");

		if(name.startsWith("#")) tag = TagKey.create(Registries.FLUID, ResourceLocation.tryParse(name.substring(1)));
		else fluid = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(name));
		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		DecayCondition.super.toNbt(nbt);
		nbt.putString("entry", tag != null ? "#" + tag.location().toString() : BuiltInRegistries.FLUID.getKey(fluid).toString());
		return nbt;
	}

	@Override
	public DecayPredicateType<? extends DecayCondition> getType() {
		return DecayPredicateType.FLUID_PREDICATE_TYPE.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
		return targetFluid != null && targetFluid.isSource() && (targetFluid.getType() == fluid || (tag != null && targetFluid.is(tag)));
	}

	@Override
	public Set<Fluid> constructApplicableFluids() {
		return fluid != null ? Set.of(fluid) : Streams.stream(BuiltInRegistries.FLUID.getTagOrEmpty(tag)).map(Holder::value).collect(Collectors.toSet());
	}

	public static FluidDecayCondition.Builder builder() {
		return new FluidDecayCondition.Builder();
	}

	public static class Builder {
		private Fluid fluid;
		private TagKey<Fluid> tag;

		public FluidDecayCondition.Builder fluid(Fluid fluid) {
			this.fluid = fluid;
			return this;
		}

		public FluidDecayCondition.Builder tag(TagKey<Fluid> tag) {
			this.tag = tag;
			return this;
		}

		public FluidDecayCondition create() {
			return new FluidDecayCondition(tag, fluid);
		}
	}
}
