package org.dimdev.dimdoors.world.decay;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

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

    public static DecayPattern deserialize(NbtCompound nbt) {
        DecayPredicate predicate = DecayPredicate.deserialize(nbt.getCompound("predicate"));
        DecayProcessor processor = DecayProcessor.deserialize(nbt.getCompound("processor"));
        return DecayPattern.builder().predicate(predicate).processor(processor).create();
    }

    public boolean test(World world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
        return predicate.test(world, pos, origin, targetBlock, targetFluid);
    }

    public void process(World world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
        ENTROPY_EVENT.invoker().entropy(world, pos, processor.process(world, pos, origin, targetBlock, targetFluid));
    }

	public Set<Block> constructApplicableBlocks() {
		return predicate.constructApplicableBlocks();
	}

	public Set<Fluid> constructApplicableFluids() {
		return predicate.constructApplicableFluids();
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
        void entropy(World world, BlockPos pos, int entorpy);
    }
}
