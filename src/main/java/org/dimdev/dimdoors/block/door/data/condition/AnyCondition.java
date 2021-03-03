package org.dimdev.dimdoors.block.door.data.condition;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public class AnyCondition extends MultipleCondition {
	public AnyCondition(List<Condition> conditions) {
		super(conditions);
	}

	public static AnyCondition fromJson(JsonObject json) {
		JsonArray conditions = json.getAsJsonArray("conditions");
		return new AnyCondition(StreamSupport.stream(conditions.spliterator(), false).map(JsonElement::getAsJsonObject).map(Condition::fromJson).collect(Collectors.toList()));
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
