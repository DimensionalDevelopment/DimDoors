package org.dimdev.dimdoors.criteria;

import java.util.Objects;

import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TagBlockBreakCriteria extends AbstractCriterion<TagBlockBreakCriteria.Conditions> {
	public static final Identifier ID = new Identifier("dimdoors", "tag_block_break");

	@Override
	protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		return new Conditions(playerPredicate, TagRegistry.block(Identifier.tryParse(obj.get("tag").getAsString())));
	}

	public void trigger(ServerPlayerEntity player, Block block) {
		this.trigger(player, c -> c.getBlockTag().contains(block));
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Tag<Block> blockTag;

		public Conditions(EntityPredicate.Extended playerPredicate, Tag<Block> blockTag) {
			super(ID, playerPredicate);
			this.blockTag = Objects.requireNonNull(blockTag);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject json = new JsonObject();
			json.addProperty("tag", ((Tag.Identified<Block>) blockTag).getId().toString());
			return super.toJson(predicateSerializer);
		}

		public Tag<Block> getBlockTag() {
			return blockTag;
		}
	}
}
