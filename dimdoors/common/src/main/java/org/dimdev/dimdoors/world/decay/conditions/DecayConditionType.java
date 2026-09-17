package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;

public record DecayConditionType<T extends DecayCondition>(MapCodec<T> codec) {
    public static final ResourceKey<Registry<DecayConditionType<? extends DecayCondition>>> KEY = ResourceKey.createRegistryKey(DimensionalDoors.id("decay_condition_type"));
    public static final Registry<DecayConditionType<? extends DecayCondition>> REGISTRY = DimensionalDoors.getSided().createRegistry(KEY);

    public static final Codec<DecayConditionType<? extends DecayCondition>> CODEC = REGISTRY.byNameCodec();

    public static final DecayConditionType<DecayCondition> NONE_CONDITION_TYPE = register(DimensionalDoors.id("none"), MapCodec.unit(DecayCondition.NONE));
    public static final DecayConditionType<SimpleDecayCondition> SIMPLE_CONDITION_TYPE = register(DimensionalDoors.id(SimpleDecayCondition.KEY), SimpleDecayCondition.CODEC);
    public static final DecayConditionType<FluidDecayCondition> FLUID_CONDITION_TYPE = register(DimensionalDoors.id(FluidDecayCondition.KEY), FluidDecayCondition.CODEC);
    public static final DecayConditionType<DecaySourceCondition> DECAY_SOURCE_CONDITION_TYPE = register(DimensionalDoors.id(DecaySourceCondition.KEY), DecaySourceCondition.CODEC);
    public static final DecayConditionType<DimensionDecayCondition> DIMENSION_CONDITION_TYPE = register(DimensionalDoors.id(DimensionDecayCondition.KEY), DimensionDecayCondition.CODEC);


    public static void register() {
    }

    static <U extends DecayCondition> DecayConditionType<U> register(ResourceLocation id, MapCodec<U> codec) {
        return DimensionalDoors.getSided().register(KEY, id, new DecayConditionType<>(codec));
    }
}
