package org.dimdev.dimdoors.world.decay;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.decay.processors.*;

import java.util.function.Supplier;

public interface DecayProcessorType<T extends DecayProcessor<?, ?>> {
    RegistrySupplier<DecayProcessorType<BlockDecayProcessor>> SIMPLE_PROCESSOR_TYPE = register(DimensionalDoors.id(BlockDecayProcessor.KEY), BlockDecayProcessor::new);
    RegistrySupplier<DecayProcessorType<NoneDecayProcessor>> NONE_PROCESSOR_TYPE = register(DimensionalDoors.id(NoneDecayProcessor.KEY), NoneDecayProcessor::instance);
    RegistrySupplier<DecayProcessorType<SelfDecayProcessor>> SELF = register(DimensionalDoors.id(SelfDecayProcessor.KEY), SelfDecayProcessor::instance);
    RegistrySupplier<DecayProcessorType<DoubleDecayProcessor>> DOUBLE_PROCESSOR_TYPE = register(DimensionalDoors.id(DoubleDecayProcessor.KEY), DoubleDecayProcessor::new);
    RegistrySupplier<DecayProcessorType<FluidDecayProcessor>> FLUID_PROCESSOR_TYPE = register(DimensionalDoors.id(FluidDecayProcessor.KEY), FluidDecayProcessor::new);

    DecayProcessor<?, ?> fromNbt(CompoundTag nbt);

    CompoundTag toNbt(CompoundTag nbt);

    static void register() {
    }

    static <T, V, U extends DecayProcessor<T, V>> RegistrySupplier<DecayProcessorType<U>> register(ResourceLocation id, Supplier<U> factory) {
        return DecayProcessor.REGISTRY.register(id, () -> new DecayProcessorType<>() {
            @Override
            public DecayProcessor<T, V> fromNbt(CompoundTag nbt) {
                return factory.get().fromNbt(nbt);
            }

            @Override
            public CompoundTag toNbt(CompoundTag nbt) {
                nbt.putString("type", id.toString());
                return nbt;
            }
        });
    }

    MapCodec<? extends DecayProcessor<?, ?>> codec();
}
