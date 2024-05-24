package org.dimdev.dimdoors.world.decay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public record DecayPattern(DecayPredicate predicate, DecayProcessor<?, ?> processor) {
    public static final Codec<DecayPattern> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DecayPredicate.CODEC.fieldOf("predicate").forGetter(DecayPattern::predicate),
            DecayProcessor.CODEC.fieldOf("processor").forGetter(DecayPattern::processor)).apply(instance, DecayPattern::new));
    public static final Event<EntropyEvent> ENTROPY_EVENT = EventFactory.of(entropyEvents -> (world, pos, entorpy) -> {
        for (EntropyEvent event : entropyEvents) event.entropy(world, pos, entorpy);
    });

    public static DecayPattern deserialize(CompoundTag nbt) {
        DecayPredicate predicate = DecayPredicate.deserialize(nbt.getCompound("predicate"));
        DecayProcessor<?, ?> processor = DecayProcessor.deserialize(nbt.getCompound("processor"));
        return new DecayPattern(predicate, processor);
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

    public Object willBecome(Object prior) {
        return processor.produces(prior);
    }

    private interface EntropyEvent {
        void entropy(Level world, BlockPos pos, int entorpy);
    }
}
