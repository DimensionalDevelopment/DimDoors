package org.dimdev.dimdoors.item.door.data.condition;

import java.util.List;

import com.mojang.serialization.MapCodec;

public abstract class MultipleCondition implements Condition {
    public static final MapCodec<List<Condition>> LIST = Condition.CODEC.listOf().fieldOf("conditions");

    protected final List<Condition> conditions;

    protected List<Condition> conditions() {
        return conditions;
    }

    protected MultipleCondition(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
