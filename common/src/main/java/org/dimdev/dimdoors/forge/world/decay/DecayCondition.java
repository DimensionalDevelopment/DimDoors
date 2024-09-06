package org.dimdev.dimdoors.forge.world.decay;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.forge.world.decay.DecayConditionType;
import org.dimdev.dimdoors.forge.world.decay.DecaySource;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public interface DecayCondition {
    Codec<DecayCondition> CODEC = DecayConditionType.CODEC.dispatch("type", DecayCondition::getType, DecayConditionType::codec);
    Codec<List<DecayCondition>> LIST_CODEC = Codec.either(CODEC, CODEC.listOf()).xmap(either -> either.map(List::of, Function.identity()), conditions -> conditions.size() > 1 ? Either.right(conditions) : Either.left(conditions.get(0)));

    DecayCondition NONE = new DecayCondition() {
        private static final String ID = "none";

        @Override
        public DecayConditionType<? extends DecayCondition> getType() {
            return DecayConditionType.NONE_CONDITION_TYPE.get();
        }

        @Override
        public boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid, DecaySource source) {
            return false;
        }
	};



    DecayConditionType<? extends DecayCondition> getType();

    boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid, DecaySource source);

	default Set<ResourceKey<Fluid>> constructApplicableFluids() {
		return Collections.emptySet();
	}

	default Set<ResourceKey<Block>> constructApplicableBlocks() {
		return Collections.emptySet();
	}

}
