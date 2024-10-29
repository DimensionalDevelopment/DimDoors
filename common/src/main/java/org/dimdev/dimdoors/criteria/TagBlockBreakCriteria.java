package org.dimdev.dimdoors.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Optional;

public class TagBlockBreakCriteria extends SimpleCriterionTrigger<TagBlockBreakCriteria.Conditions> {
	public static final String ID = "dimdoors:tag_block_break";

	@Override
	protected Conditions createInstance(JsonObject obj, Optional<ContextAwarePredicate> player, DeserializationContext predicateDeserializer) {
		return new Conditions(player, TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(obj.get("tag").getAsString())));
	}

	public void trigger(ServerPlayer player, BlockState block) {
		this.trigger(player, c -> block.is(c.getBlockTag()));
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final TagKey<Block> blockTag;

		public Conditions(Optional<ContextAwarePredicate> player, TagKey<Block> blockTag) {
			super(player);
			this.blockTag = Objects.requireNonNull(blockTag);
		}

		@Override
		public JsonObject serializeToJson() {
			JsonObject json = super.serializeToJson();
			json.addProperty("tag", blockTag.location().toString());
			return json;
		}

		public TagKey<Block> getBlockTag() {
			return blockTag;
		}
	}
}
