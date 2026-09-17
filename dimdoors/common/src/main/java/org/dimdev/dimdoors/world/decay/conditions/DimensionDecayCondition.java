package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.decay.Decay;

public class DimensionDecayCondition extends GenericDecayCondition<DimensionType> {
    public static MapCodec<DimensionDecayCondition> CODEC = CodecUtils.createCodec(DimensionDecayCondition::new, Registries.DIMENSION_TYPE);
    public static final String KEY = "dimension";

    private DimensionDecayCondition(CodecUtils.TagOrElementLocation<DimensionType> tagOrElementLocation, boolean invert) {
        super(tagOrElementLocation, invert);
    }

    public static DimensionDecayCondition of(TagKey<DimensionType> tag, boolean invert) {
        return new DimensionDecayCondition(CodecUtils.TagOrElementLocation.of(tag, Registries.DIMENSION_TYPE), invert);
    }

    public static DimensionDecayCondition of(TagKey<DimensionType> tag) {
        return new DimensionDecayCondition(CodecUtils.TagOrElementLocation.of(tag, Registries.DIMENSION_TYPE), false);
    }

    public static DimensionDecayCondition of(ResourceKey<DimensionType> key, boolean invert) {
        return new DimensionDecayCondition(CodecUtils.TagOrElementLocation.of(key, Registries.DIMENSION_TYPE), invert);
    }

    public static DimensionDecayCondition of(ResourceKey<DimensionType> key) {
        return new DimensionDecayCondition(CodecUtils.TagOrElementLocation.of(key, Registries.DIMENSION_TYPE), false);
    }

    @Override
    public DecayConditionType<? extends DecayCondition> getType() {
        return DecayConditionType.DIMENSION_CONDITION_TYPE;
    }

    @Override
    public Holder<DimensionType> getHolder(Decay.DecayContext context) {
        return context.world().dimensionTypeRegistration();
    }

    @Override
    public ResourceKey<Registry<DimensionType>> registry() {
        return Registries.DIMENSION_TYPE;
    }
}
