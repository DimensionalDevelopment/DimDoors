package org.dimdev.dimdoors.block.door.data.condition;

import com.google.gson.JsonObject;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public record InverseCondition(Condition condition) implements Condition {

	@Override
	public void toJsonInner(JsonObject json) {
		json.add("condition", condition.toJson(new JsonObject()));
	}

	@Override
	public ConditionType<?> getType() {
		return ConditionType.INVERSE;
	}

	public static InverseCondition fromJson(JsonObject json) {
		return new InverseCondition(Condition.fromJson(json.getAsJsonObject("condition")));
	}

	@Override
	public boolean matches(EntranceRiftBlockEntity rift) {
		return !this.condition.matches(rift);
	}
}
