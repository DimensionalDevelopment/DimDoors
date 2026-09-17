package org.dimdev.dimdoors.item.door.data.condition;

import com.google.gson.JsonObject;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public record InverseCondition(Condition condition) implements Condition {
    public static final MapCodec<InverseCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Condition.CODEC.fieldOf("condition").forGetter(InverseCondition::condition)).apply(instance, InverseCondition::new));

    @Override
    public ConditionType<?> getType() {
        return ConditionType.INVERSE;
    }

    @Override
    public boolean matches(EntranceRiftBlockEntity rift) {
        return !this.condition.matches(rift);
    }
}
