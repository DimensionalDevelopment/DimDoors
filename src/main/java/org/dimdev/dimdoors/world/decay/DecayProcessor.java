package org.dimdev.dimdoors.world.decay;

import java.util.function.Supplier;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.decay.processors.DoorDecayProccessor;
import org.dimdev.dimdoors.world.decay.processors.DoubleDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.SelfDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.SimpleDecayProcesor;

public interface DecayProcessor {
    Registry<DecayProcessor.DecayProcessorType<? extends DecayProcessor>> REGISTRY =
			FabricRegistryBuilder.from(new MappedRegistry<DecayProcessorType<? extends DecayProcessor>>(ResourceKey.createRegistryKey(DimensionalDoors.resource("decay_processor_type")), Lifecycle.stable(), false)).buildAndRegister();

    DecayProcessor NONE = new DecayProcessor() {
        @Override
        public DecayProcessor fromNbt(CompoundTag nbt) {
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
        public int process(Level world, BlockPos pos, BlockState origin, BlockState target) {
            return 0;
        }

        private static final String ID = "none";
    };

    static DecayProcessor deserialize(CompoundTag nbt) {
        ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
        return REGISTRY.getOptional(id).orElse(DecayProcessorType.NONE_PROCESSOR_TYPE).fromNbt(nbt);
    }

    static CompoundTag serialize(DecayProcessor modifier) {
        return modifier.toNbt(new CompoundTag());
    }


    DecayProcessor fromNbt(CompoundTag nbt);

    default CompoundTag toNbt(CompoundTag nbt) {
        return this.getType().toNbt(nbt);
    }

    DecayProcessor.DecayProcessorType<? extends DecayProcessor> getType();

    String getKey();

    int process(Level world, BlockPos pos, BlockState origin, BlockState target);

    interface DecayProcessorType<T extends DecayProcessor> {
        DecayProcessorType<SimpleDecayProcesor> SIMPLE_PROCESSOR_TYPE = register(DimensionalDoors.resource(SimpleDecayProcesor.KEY), SimpleDecayProcesor::new);
        DecayProcessorType<DecayProcessor> NONE_PROCESSOR_TYPE = register(DimensionalDoors.resource("none"), () -> NONE);
        DecayProcessorType<SelfDecayProcessor> SELF = register(DimensionalDoors.resource(SelfDecayProcessor.KEY), SelfDecayProcessor::instance);
		DecayProcessorType<? extends DecayProcessor> DOOR_PROCESSOR_TYPE = register(DimensionalDoors.resource(DoorDecayProccessor.KEY), DoorDecayProccessor::new);
		DecayProcessorType<? extends DecayProcessor> DOUBLE_PROCESSOR_TYPE = register(DimensionalDoors.resource(DoubleDecayProcessor.KEY), DoubleDecayProcessor::new);

		DecayProcessor fromNbt(CompoundTag nbt);

		CompoundTag toNbt(CompoundTag nbt);

        static void register() {
            DimensionalDoors.apiSubscribers.forEach(d -> d.registerDecayProcessors(REGISTRY));
        }

        static <U extends DecayProcessor> DecayProcessorType<U> register(ResourceLocation id, Supplier<U> factory) {
            return Registry.register(REGISTRY, id, new DecayProcessorType<U>() {
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
