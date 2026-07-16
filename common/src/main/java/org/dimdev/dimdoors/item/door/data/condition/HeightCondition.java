package org.dimdev.dimdoors.item.door.data.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.limlib.api.util.math.Equation;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import java.util.Map;

public record HeightCondition(Equation height) implements Condition {
    public static final MapCodec<HeightCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Equation.CODEC.fieldOf("height").forGetter(HeightCondition::height)
    ).apply(instance, HeightCondition::new));

    @Override
    public boolean matches(EntranceRiftBlockEntity rift) {
        var height = rift.getBlockPos().getY();

        return height().asBoolean(Map.of("height", (double) height));
    }

    @Override
    public ConditionType<?> getType() {
        return ConditionType.HEIGHT;
    }
}