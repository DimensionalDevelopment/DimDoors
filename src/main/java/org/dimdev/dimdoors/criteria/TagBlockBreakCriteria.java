package org.dimdev.dimdoors.criteria;

import java.util.Objects;

import com.google.gson.JsonObject;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TagBlockBreakCriteria extends AbstractCriterion<TagBlockBreakCriteria.Conditions> {
	public static final Identifier ID = new Identifier("dimdoors", "tag_block_break");

	@Override
	protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		return new Conditions(playerPredicate, TagKey.of(Registry.BLOCK_KEY, Identifier.tryParse(obj.get("tag").getAsString())));
	}

	public void trigger(ServerPlayerEntity player, BlockState block) {
		this.trigger(player, c -> block.isIn(c.getBlockTag()));
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final TagKey<Block> blockTag;

		public Conditions(EntityPredicate.Extended playerPredicate, TagKey<Block> blockTag) {
			super(ID, playerPredicate);
			this.blockTag = Objects.requireNonNull(blockTag);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject json = new JsonObject();
			json.addProperty("tag", blockTag.id().toString());
			return super.toJson(predicateSerializer);
		}

		public TagKey<Block> getBlockTag() {
			return blockTag;
		}
	}
}
