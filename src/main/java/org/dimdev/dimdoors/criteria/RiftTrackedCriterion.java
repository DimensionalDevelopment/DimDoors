package org.dimdev.dimdoors.criteria;

import com.google.gson.JsonObject;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RiftTrackedCriterion extends AbstractCriterion<RiftTrackedCriterion.Conditions> {
	public static final Identifier ID = new Identifier("dimdoors", "rift_tracked");

	@Override
	protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		NumberRange.FloatRange distancePredicate = NumberRange.FloatRange.fromJson(obj.get("distance"));
		return new Conditions(playerPredicate, distancePredicate);
	}

	public void trigger(ServerPlayerEntity player, BlockPos riftPos) {
		this.trigger(player, t -> t.matches(player, new Vec3d(riftPos.getX() + 0.5, riftPos.getY() + 0.5, riftPos.getZ() + 0.5)));
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static class Conditions extends AbstractCriterionConditions {
		private NumberRange.FloatRange distance;
		public Conditions(EntityPredicate.Extended playerPredicate, NumberRange.FloatRange distance) {
			super(ID, playerPredicate);
			this.distance = distance;
		}

		public boolean matches(ServerPlayerEntity player, Vec3d pos) {
			return this.distance.testSqrt(player.getPos().squaredDistanceTo(pos));
		}

		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("distance", this.distance.toJson());
			return jsonObject;
		}
	}
}
