package org.dimdev.dimdoors.item.door.data.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import java.util.List;

public class AnyCondition extends MultipleCondition {
    public static final MapCodec<AnyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, AnyCondition::new));

    public AnyCondition(List<Condition> conditions) {
		super(conditions);
	}

    @Override
	public ConditionType<?> getType() {
		return ConditionType.ANY.get();
	}

	@Override
	public boolean matches(EntranceRiftBlockEntity rift) {
		return this.conditions.stream().anyMatch(c -> c.matches(rift));
	}
}
