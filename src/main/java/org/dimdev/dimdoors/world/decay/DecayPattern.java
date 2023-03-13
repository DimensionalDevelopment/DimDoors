package org.dimdev.dimdoors.world.decay;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DecayPattern {
    public static final Event<EntropyEvent> ENTROPY_EVENT = EventFactory.createArrayBacked(EntropyEvent.class, (world, pos, entorpy) -> {}, entropyEvents -> (world, pos, entorpy) -> {
        for (EntropyEvent event : entropyEvents) event.entropy(world, pos, entorpy);
    });

    private DecayPredicate predicate;
    private DecayProcessor processor;

    public DecayPattern() {}

    public DecayPattern(DecayPredicate predicate, DecayProcessor processor) {
        this.predicate = predicate;
        this.processor = processor;
    }

    public static DecayPattern deserialize(CompoundTag nbt) {
        DecayPredicate predicate = DecayPredicate.deserialize(nbt.getCompound("predicate"));
        DecayProcessor processor = DecayProcessor.deserialize(nbt.getCompound("processor"));
        return DecayPattern.builder().predicate(predicate).processor(processor).create();
    }

    public boolean test(Level world, BlockPos pos, BlockState origin, BlockState target) {
        return predicate.test(world, pos, origin, target);
    }

    public void process(Level world, BlockPos pos, BlockState origin, BlockState target) {
        ENTROPY_EVENT.invoker().entropy(world, pos, processor.process(world, pos, origin, target));
    }

	public Set<Block> constructApplicableBlocks() {
		return predicate.constructApplicableBlocks();
	}

    public static DecayPattern.Builder builder() {
        return new DecayPattern.Builder();
    }

    public static class Builder {
        private DecayPredicate predicate = DecayPredicate.NONE;
        private DecayProcessor processor = DecayProcessor.NONE;

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
        void entropy(Level world, BlockPos pos, int entorpy);
    }
}
