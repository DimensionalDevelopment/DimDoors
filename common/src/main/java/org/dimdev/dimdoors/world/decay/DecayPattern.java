package org.dimdev.dimdoors.world.decay;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.Set;

public class DecayPattern {
    public static final Event<EntropyEvent> ENTROPY_EVENT = EventFactory.of(entropyEvents -> (world, pos, entorpy) -> {
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

    public boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
        return predicate.test(world, pos, origin, targetBlock, targetFluid);
    }

    public void process(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
        ENTROPY_EVENT.invoker().entropy(world, pos, processor.process(world, pos, origin, targetBlock, targetFluid));
    }

	public Set<Block> constructApplicableBlocks() {
		return predicate.constructApplicableBlocks();
	}

	public Set<Fluid> constructApplicableFluids() {
		return predicate.constructApplicableFluids();
	}

    public static Builder builder() {
        return new Builder();
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
