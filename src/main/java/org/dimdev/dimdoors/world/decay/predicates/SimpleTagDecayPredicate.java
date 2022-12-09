package org.dimdev.dimdoors.world.decay.predicates;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.world.decay.DecayPredicate;

import java.util.Set;
import java.util.stream.Collectors;

public class SimpleTagDecayPredicate implements DecayPredicate {
	public static final String KEY = "simple_tag";

	private TagKey<Block> block;

	public SimpleTagDecayPredicate() {}

	private SimpleTagDecayPredicate(TagKey<Block> block) {
		this.block = block;
	}

	@Override
	public DecayPredicate fromNbt(NbtCompound nbt) {
		block = TagKey.of(RegistryKeys.BLOCK, Identifier.tryParse(nbt.getString("tag")));
		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		DecayPredicate.super.toNbt(nbt);
		nbt.putString("tag", block.id().toString());
		return nbt;
	}

	@Override
	public DecayPredicateType<? extends DecayPredicate> getType() {
		return DecayPredicateType.SIMPLE_TAG_PREDICATE_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public boolean test(World world, BlockPos pos, BlockState origin, BlockState target) {
		BlockState state = world.getBlockState(pos);

		return state.isIn(block);
	}

	@Override
	public Set<Block> constructApplicableBlocks() {
		return Registries.BLOCK.getOrCreateEntryList(block).stream().map(RegistryEntry::value).collect(Collectors.toSet());
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
