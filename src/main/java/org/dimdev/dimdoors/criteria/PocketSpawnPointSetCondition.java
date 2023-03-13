package org.dimdev.dimdoors.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.dimdev.dimdoors.DimensionalDoors;

public class PocketSpawnPointSetCondition extends SimpleCriterionTrigger<PocketSpawnPointSetCondition.Conditions> {
	public static final ResourceLocation ID = DimensionalDoors.resource("pocket_spawn_point_set");

	@Override
	protected Conditions createInstance(JsonObject obj, EntityPredicate.Composite playerPredicate, DeserializationContext predicateDeserializer) {
		return new Conditions(playerPredicate);
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, t -> true);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {
		public Conditions(EntityPredicate.Composite playerPredicate) {
			super(ID, playerPredicate);
		}
	}
}
