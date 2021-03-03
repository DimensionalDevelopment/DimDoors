package org.dimdev.dimdoors.block.door.data.condition;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class MultipleCondition implements Condition {
	protected final List<Condition> conditions;

	protected MultipleCondition(List<Condition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public void toJsonInner(JsonObject json) {
		JsonArray conditionsJson = new JsonArray();
		conditions.forEach(c -> conditionsJson.add(c.toJson( new JsonObject())));
		json.add("conditions", conditionsJson);
	}
}
