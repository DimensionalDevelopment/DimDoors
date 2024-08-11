package org.dimdev.dimdoors.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class PocketSpawnPointSetCondition extends SimpleCriterionTrigger<PocketSpawnPointSetCondition.TriggerInstance> {
	public static final String ID = "pocket_spawn_point_set";

	public void trigger(ServerPlayer player) {
		this.trigger(player, t -> true);
	}

	@Override
	public Codec<TriggerInstance> codec() {
		return TriggerInstance.CODEC;
	}

	public static record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleInstance {
		public static final Codec<PocketSpawnPointSetCondition.TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(PocketSpawnPointSetCondition.TriggerInstance::player)).apply(instance, PocketSpawnPointSetCondition.TriggerInstance::new));
	}
}
