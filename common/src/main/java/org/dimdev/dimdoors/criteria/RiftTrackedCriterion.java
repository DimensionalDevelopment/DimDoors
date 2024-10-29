package org.dimdev.dimdoors.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class RiftTrackedCriterion extends SimpleCriterionTrigger<RiftTrackedCriterion.Conditions> {
	public static final String ID = "dimdoors:rift_tracked";

	@Override
	protected Conditions createInstance(JsonObject obj, Optional<ContextAwarePredicate> playerPredicate, DeserializationContext predicateDeserializer) {
		return new Conditions(playerPredicate);
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
