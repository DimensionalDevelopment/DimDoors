package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayCondition;
import org.dimdev.dimdoors.world.decay.DecayConditionType;
import org.dimdev.dimdoors.world.decay.DecaySource;

import java.util.Set;

public class FluidDecayCondition extends GenericDecayCondition<Fluid> {
	public static final Codec<FluidDecayCondition> CODEC = createCodec(FluidDecayCondition::new, Registry.FLUID.key());

	public static final String KEY = "fluid";

	public FluidDecayCondition(TagOrElementLocation<Fluid> tagOrElementLocation, boolean invert) {
		super(tagOrElementLocation, invert);
	}

	public static FluidDecayCondition of(TagKey<Fluid> tag, boolean invert) {
		return new FluidDecayCondition(TagOrElementLocation.of(tag, Registry.FLUID.key()), invert);
	}

	public static FluidDecayCondition of(TagKey<Fluid> tag) {
		return new FluidDecayCondition(TagOrElementLocation.of(tag, Registry.FLUID.key()), false);
	}

	public static FluidDecayCondition of(ResourceKey<Fluid> key, boolean invert) {
		return new FluidDecayCondition(TagOrElementLocation.of(key, Registry.FLUID.key()), invert);
	}

	public static FluidDecayCondition of(ResourceKey<Fluid> key) {
		return new FluidDecayCondition(TagOrElementLocation.of(key, Registry.FLUID.key()), false);
	}

	@Override
	public DecayConditionType<? extends DecayCondition> getType() {
		return DecayConditionType.FLUID_CONDITION_TYPE.get();
	}

	@Override
	public Holder<Fluid> getHolder(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid, DecaySource source) {
		return targetFluid.holder();
	}

	@Override
	public Set<ResourceKey<Fluid>> constructApplicableFluids() {
		return getTagOrElementLocation().getValues(Registry.FLUID);
	}
}
