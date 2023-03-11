package org.dimdev.dimdoors.world.decay.predicates;

import org.dimdev.dimdoors.world.decay.DecayPredicate;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleTagDecayPredicate implements DecayPredicate {
	public static final String KEY = "simple_tag";

	private TagKey<Block> block;

	public SimpleTagDecayPredicate() {}

	private SimpleTagDecayPredicate(TagKey<Block> block) {
		this.block = block;
	}

	@Override
	public DecayPredicate fromNbt(CompoundTag nbt) {
		block = TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(nbt.getString("tag")));
		return this;
	}

	@Override
	public CompoundTag toNbt(CompoundTag nbt) {
		DecayPredicate.super.toNbt(nbt);
		nbt.putString("tag", block.location().toString());
		return nbt;
	}

	@Override
	public DecayPredicateType<? extends DecayPredicate> getType() {
		return DecayPredicateType.SIMPLE_PREDICATE_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public boolean test(Level world, BlockPos pos, BlockState origin, BlockState target) {
		BlockState state = world.getBlockState(pos);

		return state.is(block);
	}

	@Override
	public Set<Block> constructApplicableBlocks() {
		return BuiltInRegistries.BLOCK.getOrCreateTag(block).stream().map(Holder::value).collect(Collectors.toSet());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private TagKey<Block> block = null;

		public Builder tag(TagKey<Block> block) {
			this.block = block;
			return this;
		}

		public SimpleTagDecayPredicate create() {
			return new SimpleTagDecayPredicate(block);
		}
	}
}
