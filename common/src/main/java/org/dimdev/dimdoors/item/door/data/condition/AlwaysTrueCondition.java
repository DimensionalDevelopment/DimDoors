package org.dimdev.dimdoors.item.door.data.condition;

import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public enum AlwaysTrueCondition implements Condition {
	INSTANCE;

	@Override
	public boolean matches(EntranceRiftBlockEntity rift) {
		return true;
	}

    @Override
	public ConditionType<?> getType() {
		return ConditionType.ALWAYS_TRUE.get();
	}
}
