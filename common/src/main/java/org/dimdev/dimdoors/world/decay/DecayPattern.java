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

public record DecayPattern(DecayCondition condition, DecayResult result) {
    public static final Event<EntropyEvent> ENTROPY_EVENT = EventFactory.of(entropyEvents -> (world, pos, entorpy) -> {
        for (EntropyEvent event : entropyEvents) event.entropy(world, pos, entorpy);
    });

    public static DecayPattern deserialize(CompoundTag nbt) {
        DecayCondition condition = DecayCondition.deserialize(nbt.getCompound("condition"));
        DecayResult result = DecayResult.deserialize(nbt.getCompound("result"));
        return new DecayPattern(condition, result);
    }

    public boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
        return condition.test(world, pos, origin, targetBlock, targetFluid);
    }

    public void process(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
        ENTROPY_EVENT.invoker().entropy(world, pos, result.process(world, pos, origin, targetBlock, targetFluid));
    }

	public Set<Block> constructApplicableBlocks() {
		return condition.constructApplicableBlocks();
	}

	public Set<Fluid> constructApplicableFluids() {
		return condition.constructApplicableFluids();
	}

    public Object willBecome(Object prior) {
        return result.produces(prior);
    }

    private interface EntropyEvent {
        void entropy(Level world, BlockPos pos, int entorpy);
    }
}
