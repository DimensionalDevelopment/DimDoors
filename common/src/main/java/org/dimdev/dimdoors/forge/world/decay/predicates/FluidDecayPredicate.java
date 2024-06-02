package org.dimdev.dimdoors.forge.world.decay.predicates;

import com.google.common.collect.Streams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.forge.world.decay.DecayPredicate;

import java.util.Set;
import java.util.stream.Collectors;

public class FluidDecayPredicate implements DecayPredicate {
	public static final String KEY = "fluid";

	private Fluid fluid;
	private TagKey<Fluid> tag;

	public FluidDecayPredicate() {}

	public FluidDecayPredicate(TagKey<Fluid> tag, Fluid fluid) {
		this.tag = tag;
		this.fluid = fluid;
	}

	@Override
	public DecayPredicate fromNbt(CompoundTag nbt) {
		String name = nbt.getString("entry");

		if(name.startsWith("#")) tag = TagKey.create(Registry.FLUID.key(), ResourceLocation.tryParse(name.substring(1)));
		else fluid = Registry.FLUID.get(ResourceLocation.tryParse(name));
		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		DecayPredicate.super.toNbt(nbt);
		nbt.putString("entry", tag != null ? "#" + tag.location().toString() : Registry.FLUID.getKey(fluid).toString());
		return nbt;
	}

	@Override
	public DecayPredicateType<? extends DecayPredicate> getType() {
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
		return fluid != null ? Set.of(fluid) : Streams.stream(Registry.FLUID.getTagOrEmpty(tag)).map(Holder::value).collect(Collectors.toSet());
	}

	public static FluidDecayPredicate.Builder builder() {
		return new FluidDecayPredicate.Builder();
	}

	public static class Builder {
		private Fluid fluid;
		private TagKey<Fluid> tag;

		public FluidDecayPredicate.Builder fluid(Fluid fluid) {
			this.fluid = fluid;
			return this;
		}

		public FluidDecayPredicate.Builder tag(TagKey<Fluid> tag) {
			this.tag = tag;
			return this;
		}

		public FluidDecayPredicate create() {
			return new FluidDecayPredicate(tag, fluid);
		}
	}
}
