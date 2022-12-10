package org.dimdev.dimdoors.world.decay;

import java.util.function.Supplier;

import com.mojang.serialization.Lifecycle;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.decay.processors.DoorDecayProccessor;
import org.dimdev.dimdoors.world.decay.processors.DoubleDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.SelfDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.SimpleDecayProcesor;

public interface DecayProcessor {
    Registry<DecayProcessor.DecayProcessorType<? extends DecayProcessor>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<DecayProcessorType<? extends DecayProcessor>>(RegistryKey.ofRegistry(DimensionalDoors.id("decay_processor_type")), Lifecycle.stable(), false)).buildAndRegister();

    DecayProcessor NONE = new DecayProcessor() {
        @Override
        public DecayProcessor fromNbt(NbtCompound nbt) {
            return this;
        }

        @Override
        public DecayProcessorType<? extends DecayProcessor> getType() {
            return DecayProcessor.DecayProcessorType.NONE_PROCESSOR_TYPE;
        }

        @Override
        public String getKey() {
            return ID;
        }

        @Override
        public int process(World world, BlockPos pos, BlockState origin, BlockState target) {
            return 0;
        }

        private static final String ID = "none";
    };

    static DecayProcessor deserialize(NbtCompound nbt) {
        Identifier id = Identifier.tryParse(nbt.getString("type"));
        return REGISTRY.getOrEmpty(id).orElse(DecayProcessorType.NONE_PROCESSOR_TYPE).fromNbt(nbt);
    }

    static NbtCompound serialize(DecayProcessor modifier) {
        return modifier.toNbt(new NbtCompound());
    }


    DecayProcessor fromNbt(NbtCompound nbt);

    default NbtCompound toNbt(NbtCompound nbt) {
        return this.getType().toNbt(nbt);
    }

    DecayProcessor.DecayProcessorType<? extends DecayProcessor> getType();

    String getKey();

    int process(World world, BlockPos pos, BlockState origin, BlockState target);

    interface DecayProcessorType<T extends DecayProcessor> {
        DecayProcessorType<SimpleDecayProcesor> SIMPLE_PROCESSOR_TYPE = register(DimensionalDoors.id(SimpleDecayProcesor.KEY), SimpleDecayProcesor::new);
        DecayProcessorType<DecayProcessor> NONE_PROCESSOR_TYPE = register(DimensionalDoors.id("none"), () -> NONE);
        DecayProcessorType<SelfDecayProcessor> SELF = register(DimensionalDoors.id(SelfDecayProcessor.KEY), SelfDecayProcessor::instance);
		DecayProcessorType<? extends DecayProcessor> DOOR_PROCESSOR_TYPE = register(DimensionalDoors.id(DoorDecayProccessor.KEY), DoorDecayProccessor::new);
		DecayProcessorType<? extends DecayProcessor> DOUBLE_PROCESSOR_TYPE = register(DimensionalDoors.id(DoubleDecayProcessor.KEY), DoubleDecayProcessor::new);

		DecayProcessor fromNbt(NbtCompound nbt);

		NbtCompound toNbt(NbtCompound nbt);

        static void register() {
            DimensionalDoors.apiSubscribers.forEach(d -> d.registerDecayProcessors(REGISTRY));
        }

        static <U extends DecayProcessor> DecayProcessorType<U> register(Identifier id, Supplier<U> factory) {
            return Registry.register(REGISTRY, id, new DecayProcessorType<U>() {
                @Override
                public DecayProcessor fromNbt(NbtCompound nbt) {
                    return factory.get().fromNbt(nbt);
                }

                @Override
                public NbtCompound toNbt(NbtCompound nbt) {
                    nbt.putString("type", id.toString());
                    return nbt;
                }
            });
        }
    }
}
