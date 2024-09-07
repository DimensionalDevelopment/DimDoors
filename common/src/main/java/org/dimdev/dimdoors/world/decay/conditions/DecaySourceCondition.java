package org.dimdev.dimdoors.world.decay.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayCondition;
import org.dimdev.dimdoors.world.decay.DecayConditionType;
import org.dimdev.dimdoors.world.decay.DecaySource;

public record DecaySourceCondition(DecaySource source) implements DecayCondition {
    public static Codec<DecaySourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(StringRepresentable.fromEnum(DecaySource::values).fieldOf("source").forGetter(DecaySourceCondition::source)).apply(instance, DecaySourceCondition::new));
    public static String KEY = "decay_source";

    @Override
    public DecayConditionType<? extends DecayCondition> getType() {
        return DecayConditionType.DECAY_SOURCE_CONDITION_TYPE.get();
    }

    @Override
    public boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid, DecaySource source) {
        return this.source == source;
    }
}
