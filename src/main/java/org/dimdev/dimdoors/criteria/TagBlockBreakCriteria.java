package org.dimdev.dimdoors.criteria;

import java.util.Objects;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.google.gson.JsonObject;
import org.dimdev.dimdoors.DimensionalDoors;

public class TagBlockBreakCriteria extends SimpleCriterionTrigger<TagBlockBreakCriteria.Conditions> {
	public static final ResourceLocation ID = DimensionalDoors.resource("tag_block_break");

	@Override
	protected Conditions createInstance(JsonObject obj, EntityPredicate.Composite playerPredicate, DeserializationContext predicateDeserializer) {
		return new Conditions(playerPredicate, TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(obj.get("tag").getAsString())));
	}

	public void trigger(ServerPlayer player, BlockState block) {
		this.trigger(player, c -> block.is(c.getBlockTag()));
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public static class Conditions extends AbstractCriterionTriggerInstance {
		private final TagKey<Block> blockTag;

		public Conditions(EntityPredicate.Composite playerPredicate, TagKey<Block> blockTag) {
			super(ID, playerPredicate);
			this.blockTag = Objects.requireNonNull(blockTag);
		}

		@Override
		public JsonObject serializeToJson(SerializationContext predicateSerializer) {
			JsonObject json = new JsonObject();
			json.addProperty("tag", blockTag.location().toString());
			return super.serializeToJson(predicateSerializer);
		}

		public TagKey<Block> getBlockTag() {
			return blockTag;
		}
	}
}
