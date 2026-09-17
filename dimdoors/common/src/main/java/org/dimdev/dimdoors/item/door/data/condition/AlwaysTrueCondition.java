package org.dimdev.dimdoors.item.door.data.condition;

import com.google.gson.JsonObject;

import com.mojang.serialization.MapCodec;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public enum AlwaysTrueCondition implements Condition {
    INSTANCE;

    public static final MapCodec<AlwaysTrueCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean matches(EntranceRiftBlockEntity rift) {
    return true;
    }

    @Override
    public ConditionType<?> getType() {
    return ConditionType.ALWAYS_TRUE;
    }
}
