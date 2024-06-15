package org.dimdev.dimdoors.world.decay;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.decay.results.*;

import java.util.function.Supplier;

public interface DecayResultType<T extends DecayResult> {
    RegistrySupplier<DecayResultType<BlockDecayResult>> SIMPLE_PROCESSOR_TYPE = register(DimensionalDoors.id(BlockDecayResult.KEY), BlockDecayResult::new);
    RegistrySupplier<DecayResultType<NoneDecayResult>> NONE_PROCESSOR_TYPE = register(DimensionalDoors.id(NoneDecayResult.KEY), NoneDecayResult::instance);
    RegistrySupplier<DecayResultType<SelfDecayResult>> SELF = register(DimensionalDoors.id(SelfDecayResult.KEY), SelfDecayResult::instance);
    RegistrySupplier<DecayResultType<DoubleDecayResult>> DOUBLE_PROCESSOR_TYPE = register(DimensionalDoors.id(DoubleDecayResult.KEY), DoubleDecayResult::new);
    RegistrySupplier<DecayResultType<FluidDecayResult>> FLUID_PROCESSOR_TYPE = register(DimensionalDoors.id(FluidDecayResult.KEY), FluidDecayResult::new);

    DecayResult fromNbt(CompoundTag nbt);

    CompoundTag toNbt(CompoundTag nbt);

    static void register() {
    }

    static <T, V, U extends DecayResult> RegistrySupplier<DecayResultType<U>> register(ResourceLocation id, Supplier<U> factory) {
        return DecayResult.REGISTRY.register(id, () -> new DecayResultType<>() {
            @Override
            public DecayResult fromNbt(CompoundTag nbt) {
                return factory.get().fromNbt(nbt);
            }

            @Override
            public CompoundTag toNbt(CompoundTag nbt) {
                nbt.putString("type", id.toString());
                return nbt;
            }
        });
    }
}
