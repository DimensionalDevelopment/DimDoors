package org.dimdev.dimdoors.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class TagBlockBreakCriteria extends SimpleCriterionTrigger<TagBlockBreakCriteria.TriggerInstance> {
	public static final String ID = "tag_block_break";


	public void trigger(ServerPlayer player, BlockState block) {
		this.trigger(player, c -> block.is(c.tagKey()));
	}

	@Override
	public Codec<TagBlockBreakCriteria.TriggerInstance> codec() {
		return TriggerInstance.CODEC;
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, TagKey<Block> tagKey) implements SimpleInstance {
		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(TriggerInstance::tagKey)).apply(instance, TriggerInstance::new));
	}
}
