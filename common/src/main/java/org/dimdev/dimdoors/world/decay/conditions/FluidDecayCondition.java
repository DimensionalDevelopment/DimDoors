package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.decay.Decay;

public class FluidDecayCondition extends GenericDecayCondition<Fluid> {
	public static final MapCodec<FluidDecayCondition> CODEC = CodecUtils.createCodec(FluidDecayCondition::new, Registries.FLUID);

	public static final String KEY = "fluid";

	public FluidDecayCondition(CodecUtils.TagOrElementLocation<Fluid> tagOrElementLocation, boolean invert) {
		super(tagOrElementLocation, invert);
	}

	public static FluidDecayCondition of(TagKey<Fluid> tag, boolean invert) {
		return new FluidDecayCondition(CodecUtils.TagOrElementLocation.of(tag, Registries.FLUID), invert);
	}

	public static FluidDecayCondition of(TagKey<Fluid> tag) {
		return new FluidDecayCondition(CodecUtils.TagOrElementLocation.of(tag, Registries.FLUID), false);
	}

	public static FluidDecayCondition of(ResourceKey<Fluid> key, boolean invert) {
		return new FluidDecayCondition(CodecUtils.TagOrElementLocation.of(key, Registries.FLUID), invert);
	}

	public static FluidDecayCondition of(ResourceKey<Fluid> key) {
		return new FluidDecayCondition(CodecUtils.TagOrElementLocation.of(key, Registries.FLUID), false);
	}

	@Override
	public DecayConditionType<? extends DecayCondition> getType() {
		return DecayConditionType.FLUID_CONDITION_TYPE.get();
	}

	@Override
	public Holder<Fluid> getHolder(Decay.DecayContext context) {
		return context.targetFluidState().holder();
	}

    @Override
    public ResourceKey<Registry<Fluid>> registry() {
        return Registries.FLUID;
    }
}
