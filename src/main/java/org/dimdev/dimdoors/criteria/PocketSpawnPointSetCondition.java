package org.dimdev.dimdoors.criteria;

import com.google.gson.JsonObject;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PocketSpawnPointSetCondition extends AbstractCriterion<PocketSpawnPointSetCondition.Conditions> {
	public static final Identifier ID = new Identifier("dimdoors", "pocket_spawn_point_set");

	@Override
	protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		return new Conditions(playerPredicate);
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, t -> true);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions(EntityPredicate.Extended playerPredicate) {
			super(ID, playerPredicate);
		}
	}
}
