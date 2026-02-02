package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecaySource;

public record DecaySourceCondition(DecaySource source) implements DecayCondition {
    public static MapCodec<DecaySourceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DecaySource.CODEC.fieldOf("source").forGetter(DecaySourceCondition::source)
    ).apply(instance, DecaySourceCondition::new));
    public static String KEY = "decay_source";

    @Override
    public DecayConditionType<? extends DecayCondition> getType() {
        return DecayConditionType.DECAY_SOURCE_CONDITION_TYPE.get();
    }

    @Override
    public boolean test(Decay.DecayContext context) {
        return this.source == context.source();
    }
}
