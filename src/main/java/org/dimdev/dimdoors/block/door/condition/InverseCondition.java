package org.dimdev.dimdoors.block.door.condition;

import com.google.gson.JsonObject;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public class InverseCondition implements Condition {
	private final Condition condition;

	public InverseCondition(Condition condition) {
		this.condition = condition;
	}

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
