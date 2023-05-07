package org.dimdev.dimdoors.world.decay;

import java.util.function.Supplier;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.datagen.FluidDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.*;

public interface DecayProcessor {
    Registrar<DecayProcessorType<? extends DecayProcessor>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<DecayProcessorType<? extends DecayProcessor>>builder(DimensionalDoors.id("decay_processor_type")).build();

    DecayProcessor NONE = new DecayProcessor() {
        @Override
        public DecayProcessor fromNbt(CompoundTag nbt) {
            return this;
        }

        @Override
        public DecayProcessorType<? extends DecayProcessor> getType() {
            return DecayProcessorType.NONE_PROCESSOR_TYPE;
        }

        @Override
        public String getKey() {
            return ID;
        }

        @Override
        public int process(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
            return 0;
        }

        private static final String ID = "none";
    };

    static DecayProcessor deserialize(CompoundTag nbt) {
        ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
        return REGISTRY.delegate(id).orElse(DecayProcessorType.NONE_PROCESSOR_TYPE).fromNbt(nbt);
    }

    static CompoundTag serialize(DecayProcessor modifier) {
        return modifier.toNbt(new CompoundTag());
    }


    DecayProcessor fromNbt(CompoundTag nbt);

    default CompoundTag toNbt(CompoundTag nbt) {
        return this.getType().toNbt(nbt);
    }

    DecayProcessorType<? extends DecayProcessor> getType();

    String getKey();

    int process(Level world, BlockPos pos, BlockState origin, BlockState targetState, FluidState targetFluid);

    interface DecayProcessorType<T extends DecayProcessor> {
        RegistrySupplier<DecayProcessorType<BlockDecayProcessor>> SIMPLE_PROCESSOR_TYPE = register(DimensionalDoors.id(BlockDecayProcessor.KEY), BlockDecayProcessor::new);
        RegistrySupplier<DecayProcessorType<DecayProcessor>> NONE_PROCESSOR_TYPE = register(DimensionalDoors.id("none"), () -> NONE);
        RegistrySupplier<DecayProcessorType<SelfDecayProcessor>> SELF = register(DimensionalDoors.id(SelfDecayProcessor.KEY), SelfDecayProcessor::instance);
		RegistrySupplier<DecayProcessorType<DoorDecayProccessor>> DOOR_PROCESSOR_TYPE = register(DimensionalDoors.id(DoorDecayProccessor.KEY), DoorDecayProccessor::new);
		RegistrySupplier<DecayProcessorType<DoubleDecayProcessor>> DOUBLE_PROCESSOR_TYPE = register(DimensionalDoors.id(DoubleDecayProcessor.KEY), DoubleDecayProcessor::new);
		RegistrySupplier<DecayProcessorType<FluidDecayProcessor>> FLUID_PROCESSOR_TYPE = register(DimensionalDoors.id(FluidDecayProcessor.KEY), FluidDecayProcessor::new);

		DecayProcessor fromNbt(CompoundTag nbt);

		CompoundTag toNbt(CompoundTag nbt);

        static void register() {}

        static <U extends DecayProcessor> RegistrySupplier<DecayProcessorType<U>> register(ResourceLocation id, Supplier<U> factory) {
            return REGISTRY.register(id, new DecayProcessorType<U>() {
                @Override
                public DecayProcessor fromNbt(CompoundTag nbt) {
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
}
