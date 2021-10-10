package org.dimdev.dimdoors.world.decay;

import com.google.gson.JsonObject;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class DecayPattern {
    public static final Event<EntropyEvent> ENTROPY_EVENT = EventFactory.createArrayBacked(EntropyEvent.class, (world, pos, entorpy) -> { System.out.println("Entorpy value of " + entorpy + " at " + pos + " in " + world.getRegistryKey().getValue()); }, entropyEvents -> (world, pos, entorpy) -> {
        for (EntropyEvent event : entropyEvents) event.entropy(world, pos, entorpy);
    });

    private DecayPredicate predicate;
    private DecayProcessor processor;

    public DecayPattern() {}

    public DecayPattern(DecayPredicate predicate, DecayProcessor processor) {
        this.predicate = predicate;
        this.processor = processor;
    }

    public static DecayPattern deserialize(JsonObject nbt) {
        DecayPredicate predicate = DecayPredicate.deserialize(nbt.getAsJsonObject("predicate"));
        DecayProcessor processor = DecayProcessor.deserialize(nbt.getAsJsonObject("processor"));
        return DecayPattern.builder().predicate(predicate).processor(processor).create();
    }

    public boolean test(World world, BlockPos pos, BlockState origin, BlockState target) {
        return predicate.test(world, pos, origin, target);
    }

    public void process(World world, BlockPos pos, BlockState origin, BlockState target) {
        ENTROPY_EVENT.invoker().entropy(world, pos, processor.process(world, pos, origin, target));
    }

    public static DecayPattern.Builder builder() {
        return new DecayPattern.Builder();
    }

    public static class Builder {
        private DecayPredicate predicate = DecayPredicate.DUMMY;
        private DecayProcessor processor = DecayProcessor.DUMMY;

        public Builder predicate(DecayPredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder processor(DecayProcessor processor) {
            this.processor = processor;
            return this;
        }

        public DecayPattern create() {
            return new DecayPattern(predicate, processor);
        }
    }

    private interface EntropyEvent {
        void entropy(World world, BlockPos pos, int entorpy);
    }
}
