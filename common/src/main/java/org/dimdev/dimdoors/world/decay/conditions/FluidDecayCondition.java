package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.decay.Decay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class FluidDecayCondition extends GenericDecayCondition<Fluid> {
	public static final MapCodec<FluidDecayCondition> CODEC = RecordCodecBuilder
            .mapCodec(
                    instance -> CodecUtils.decayConditionFields(instance, Registries.FLUID)
                            .and(StringRepresentable.<Type>fromEnum(Type::values)
                                    .optionalFieldOf("type", Type.BOTH)
                                    .forGetter(a -> a.type
                                    )
                            ).apply(instance, FluidDecayCondition::new)
            );

	public static final String KEY = "fluid";
    private final Type type;

    public FluidDecayCondition(CodecUtils.TagOrElementLocation<Fluid> tagOrElementLocation, boolean invert, Type type) {
		super(tagOrElementLocation, invert);
        this.type = type;
	}

	public static FluidDecayCondition of(TagKey<Fluid> tag, boolean invert, Type type) {
		return new FluidDecayCondition(CodecUtils.TagOrElementLocation.of(tag, Registries.FLUID), invert, type);
	}

    public static FluidDecayCondition of(TagKey<Fluid> tag, boolean invert) {
        return of(tag, invert, Type.BOTH);
    }

	public static FluidDecayCondition of(TagKey<Fluid> tag) {
		return of(tag, false);
	}

    public static FluidDecayCondition of(TagKey<Fluid> tag, Type type) {
        return of(tag, false, type);
    }

    public static FluidDecayCondition of(ResourceKey<Fluid> key, boolean invert) {
		return of(key, invert, Type.BOTH);
	}

    public static FluidDecayCondition of(ResourceKey<? extends Fluid> key, boolean invert, Type type) {
        return new FluidDecayCondition(CodecUtils.TagOrElementLocation.of((ResourceKey<Fluid>) key, Registries.FLUID), invert, type);
    }

    public static FluidDecayCondition of(ResourceKey<Fluid> key) {
        return of(key, Type.BOTH);
    }

    public static FluidDecayCondition of(ResourceKey<Fluid> key, Type type) {
        return of(key, false, type);
    }


    public static FluidDecayCondition of(Fluid key) {
		return of(key, Type.BOTH);
	}

    public static FluidDecayCondition of(Fluid key, Type type) {
        return of(key, false, type);
    }

    public static FluidDecayCondition of(Fluid key, boolean invert, Type type) {
        return of(key.builtInRegistryHolder().key(), invert, type);
    }


    @Override
	public DecayConditionType<? extends DecayCondition> getType() {
		return DecayConditionType.FLUID_CONDITION_TYPE.get();
	}

    @Override
    public boolean test(Decay.DecayContext context) {
        return super.test(context) && (type == Type.BOTH || (context.targetFluidState().isSource() && type == Type.SOURCE));
    }

    @Override
	public Holder<Fluid> getHolder(Decay.DecayContext context) {
		return context.targetFluidState().holder();
	}

    @Override
    public ResourceKey<Registry<Fluid>> registry() {
        return Registries.FLUID;
    }

    public static enum Type implements StringRepresentable {
        FLOWING, SOURCE, BOTH;

        private final String serializedName;

        private Type() {
            this.serializedName = name().toLowerCase();
        }

        @Override
        public @NotNull String getSerializedName() {
            return serializedName;
        }
    }
}
