package org.dimdev.dimdoors.world.decay.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecaySource;
import org.dimdev.dimdoors.world.decay.conditions.Applicator;
import org.dimdev.dimdoors.world.decay.conditions.DecayCondition;
import org.dimdev.dimdoors.world.decay.results.DecayResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public record CompoundDecayPattern(List<DecayCondition> conditions, DecayResult result) implements DecayPattern {
    public static final String KEY = "compound";

    public static final MapCodec<CompoundDecayPattern> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DecayCondition.LIST_CODEC.fieldOf("conditions").forGetter(CompoundDecayPattern::conditions),
            DecayResult.CODEC.fieldOf("result").forGetter(CompoundDecayPattern::result)
    ).apply(instance, CompoundDecayPattern::new));

    public boolean test(Decay.DecayContext context) {
        return conditions.stream().allMatch(condition -> condition.test(context));
    }

    public int process(Decay.DecayContext context) {
        return result.process(context);
    }

    public Stream<ResourceKey<?>> constructApplicable(RegistryAccess access) {
        return conditions.stream().filter(a -> a instanceof Applicator<?>).map(a -> (Applicator<?>) a).flatMap(a -> a.constructApplicable(access));
    }

    @Override
    public DecayPatternType<? extends DecayPattern> getType() {
        return DecayPatternType.COMPOUND;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements DecayPattern.Builder<CompoundDecayPattern> {
        private final List<DecayCondition> conditions = new ArrayList<>();
        private DecayResult result;

        private Builder() {}

        public Builder condition(DecayCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public Builder conditions(DecayCondition... conditions) {
            this.conditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public Builder conditions(List<DecayCondition> conditions) {
            this.conditions.addAll(conditions);
            return this;
        }

        public Builder result(DecayResult result) {
            this.result = result;
            return this;
        }

        public CompoundDecayPattern build(HolderLookup.Provider provider) {
            if (result == null) {
                throw new IllegalStateException("DecayResult must be set before building");
            }
            if (conditions.isEmpty()) {
                throw new IllegalStateException("At least one DecayCondition must be added before building");
            }
            return new CompoundDecayPattern(List.copyOf(conditions), result);
        }
    }
}
