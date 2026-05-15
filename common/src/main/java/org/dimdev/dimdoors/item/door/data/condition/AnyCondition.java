package org.dimdev.dimdoors.item.door.data.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AnyCondition extends MultipleCondition {
    public static MapCodec<AnyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(LIST.forGetter(MultipleCondition::conditions)).apply(instance, AnyCondition::new));

    public AnyCondition(List<Condition> conditions) {
    super(conditions);
    }

    @Override
    public ConditionType<?> getType() {
    return ConditionType.ANY;
    }

    @Override
    public boolean matches(EntranceRiftBlockEntity rift) {
        return this.conditions.stream().anyMatch(c -> c.matches(rift));
    }
}
