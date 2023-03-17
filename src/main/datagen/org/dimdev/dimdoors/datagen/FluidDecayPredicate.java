package org.dimdev.dimdoors.datagen;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.dimdev.dimdoors.world.decay.DecayPredicate;
import org.dimdev.dimdoors.world.decay.predicates.SimpleDecayPredicate;

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
	public DecayPredicate fromNbt(NbtCompound nbt) {
		String name = nbt.getString("entry");

		if(name.startsWith("#")) tag = TagKey.of(RegistryKeys.FLUID, Identifier.tryParse(name.substring(1)));
		else fluid = Registries.FLUID.get(Identifier.tryParse(name));
		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		DecayPredicate.super.toNbt(nbt);
		nbt.putString("entry", tag != null ? "#" + tag.id().toString() : Registries.FLUID.getId(fluid).toString());
		return nbt;
	}

	@Override
	public DecayPredicateType<? extends DecayPredicate> getType() {
		return DecayPredicateType.FLUID_PREDICATE_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public boolean test(World world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
		return targetFluid.isStill() && (targetFluid.getFluid() == fluid || targetFluid.isIn(tag));
	}

	@Override
	public Set<Fluid> constructApplicableFluids() {
		return fluid != null ? Set.of(fluid) : Streams.stream(Registries.FLUID.iterateEntries(tag)).map(RegistryEntry::value).collect(Collectors.toSet());
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
