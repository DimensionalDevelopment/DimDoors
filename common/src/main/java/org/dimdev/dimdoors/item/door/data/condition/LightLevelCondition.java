package org.dimdev.dimdoors.item.door.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.api.util.math.Equation;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import java.util.Map;

public record LightLevelCondition(Equation lightLevel) implements Condition {
    public static final MapCodec<LightLevelCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Equation.CODEC.fieldOf("light_level").forGetter(LightLevelCondition::lightLevel)
    ).apply(instance, LightLevelCondition::new));

    @Override
    public boolean matches(EntranceRiftBlockEntity rift) {
        var lightLevel = rift.getLevel().getLightEmission(rift.getBlockPos());

        return lightLevel().asBoolean(Map.of("light", (double) lightLevel));
    }

    @Override
    public ConditionType<?> getType() {
        return ConditionType.LIGHT_LEVEL;
    }
}
