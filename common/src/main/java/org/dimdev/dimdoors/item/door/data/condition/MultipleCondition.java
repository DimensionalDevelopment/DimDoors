package org.dimdev.dimdoors.item.door.data.condition;

import java.util.List;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public abstract class MultipleCondition implements Condition {
    public static <T extends MultipleCondition> Products.P1<RecordCodecBuilder.Mu<T>, List<Condition>> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Condition.CODEC.listOf().fieldOf("conditions").forGetter(a -> a.conditions)
        );
    }


	protected final List<Condition> conditions;

	protected MultipleCondition(List<Condition> conditions) {
		this.conditions = conditions;
	}

}
