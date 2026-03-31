package org.dimdev.dimdoors.item.door.data.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AllCondition extends MultipleCondition {
    public static final MapCodec<AllCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, AllCondition::new));

	public AllCondition(List<Condition> conditions) {
		super(conditions);
	}

	@Override
	public ConditionType<?> getType() {
		return ConditionType.ALL.get();
	}

	@Override
	public boolean matches(EntranceRiftBlockEntity rift) {
		return this.conditions.stream().allMatch(c -> c.matches(rift));
	}
}
