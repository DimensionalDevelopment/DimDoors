package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayCondition;
import org.dimdev.dimdoors.world.decay.DecayConditionType;
import org.dimdev.dimdoors.world.decay.DecaySource;

public class DimensionDecayCondition extends GenericDecayCondition<DimensionType> {
    public static Codec<DimensionDecayCondition> CODEC = createCodec(DimensionDecayCondition::new, Registries.DIMENSION_TYPE);
    public static final String KEY = "dimension";

    private DimensionDecayCondition(TagOrElementLocation<DimensionType> tagOrElementLocation, boolean invert) {
        super(tagOrElementLocation, invert);
    }

    public static DimensionDecayCondition of(TagKey<DimensionType> tag, boolean invert) {
        return new DimensionDecayCondition(TagOrElementLocation.of(tag, Registries.DIMENSION_TYPE), invert);
    }

    public static DimensionDecayCondition of(TagKey<DimensionType> tag) {
        return new DimensionDecayCondition(TagOrElementLocation.of(tag, Registries.DIMENSION_TYPE), false);
    }

    public static DimensionDecayCondition of(ResourceKey<DimensionType> key, boolean invert) {
        return new DimensionDecayCondition(TagOrElementLocation.of(key, Registries.DIMENSION_TYPE), invert);
    }

    public static DimensionDecayCondition of(ResourceKey<DimensionType> key) {
        return new DimensionDecayCondition(TagOrElementLocation.of(key, Registries.DIMENSION_TYPE), false);
    }

    @Override
    public DecayConditionType<? extends DecayCondition> getType() {
        return DecayConditionType.DIMENSION_CONDITION_TYPE.get();
    }

    @Override
    public Holder<DimensionType> getHolder(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid, DecaySource source) {
        return world.dimensionTypeRegistration();
    }
}
