package org.dimdev.dimdoors.world.decay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.decay.conditions.DecaySourceCondition;
import org.dimdev.dimdoors.world.decay.conditions.DimensionDecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.FluidDecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.SimpleDecayCondition;

public record DecayConditionType<T extends DecayCondition>(MapCodec<T> codec) {
    public static final Registrar<DecayConditionType<? extends DecayCondition>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<DecayConditionType<? extends DecayCondition>>builder(DimensionalDoors.id("decay_condition_type")).build();

    public static final Codec<DecayConditionType<?>> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);

    public static final RegistrySupplier<DecayConditionType<DecayCondition>> NONE_CONDITION_TYPE = register(DimensionalDoors.id("none"), MapCodec.unit(DecayCondition.NONE));
    public static final RegistrySupplier<DecayConditionType<SimpleDecayCondition>> SIMPLE_CONDITION_TYPE = register(DimensionalDoors.id(SimpleDecayCondition.KEY), SimpleDecayCondition.CODEC);
    public static final RegistrySupplier<DecayConditionType<FluidDecayCondition>> FLUID_CONDITION_TYPE = register(DimensionalDoors.id(FluidDecayCondition.KEY), FluidDecayCondition.CODEC);
    public static final RegistrySupplier<DecayConditionType<DecaySourceCondition>> DECAY_SOURCE_CONDITION_TYPE = register(DimensionalDoors.id(DecaySourceCondition.KEY), DecaySourceCondition.CODEC);
    public static final RegistrySupplier<DecayConditionType<DimensionDecayCondition>> DIMENSION_CONDITION_TYPE = register(DimensionalDoors.id(DimensionDecayCondition.KEY), DimensionDecayCondition.CODEC);


    public static void register() {
    }

    static <U extends DecayCondition> RegistrySupplier<DecayConditionType<U>> register(ResourceLocation id, MapCodec<U> codec) {
        return REGISTRY.register(id, () -> new DecayConditionType<>(codec));
    }
}
