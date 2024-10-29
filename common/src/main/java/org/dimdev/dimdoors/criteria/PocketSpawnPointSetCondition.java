package org.dimdev.dimdoors.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class PocketSpawnPointSetCondition extends SimpleCriterionTrigger<PocketSpawnPointSetCondition.Conditions> {
	public static final String ID = "dimdoors:pocket_spawn_point_set";

	@Override
	protected Conditions createInstance(JsonObject jsonObject, Optional<ContextAwarePredicate> composite, DeserializationContext deserializationContext) {
		return new Conditions(composite);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, t -> true);
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {
		public Conditions(Optional<ContextAwarePredicate> playerPredicate) {
			super(playerPredicate);
		}
	}
}
