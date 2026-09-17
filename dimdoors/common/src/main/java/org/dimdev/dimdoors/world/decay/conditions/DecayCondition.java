package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.world.decay.Decay;

import java.util.List;
import java.util.function.Function;

public interface DecayCondition {
    Codec<DecayCondition> CODEC = DecayConditionType.CODEC.dispatch("type", DecayCondition::getType, DecayConditionType::codec);
    Codec<List<DecayCondition>> LIST_CODEC = Codec.either(CODEC, CODEC.listOf()).xmap(either -> either.map(List::of, Function.identity()), conditions -> conditions.size() > 1 ? Either.right(conditions) : Either.left(conditions.get(0)));

    DecayCondition NONE = new DecayCondition() {
        private static final String ID = "none";

        @Override
        public DecayConditionType<? extends DecayCondition> getType() {
            return DecayConditionType.NONE_CONDITION_TYPE;
        }

        @Override
        public boolean test(Decay.DecayContext context) {
            return false;
        }
    };



    DecayConditionType<? extends DecayCondition> getType();

    boolean test(Decay.DecayContext context);
}
