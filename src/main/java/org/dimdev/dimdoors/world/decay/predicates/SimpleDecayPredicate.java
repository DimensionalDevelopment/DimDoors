package org.dimdev.dimdoors.world.decay.predicates;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.dimdev.dimdoors.world.decay.DecayPredicate;

public class SimpleDecayPredicate implements DecayPredicate {
    public static final String KEY = "block";

    private Block block;
	private TagKey<Block> tag;

    public SimpleDecayPredicate() {}

	public SimpleDecayPredicate(TagKey<Block> tag, Block block) {
		this.tag = tag;
		this.block = block;
	}

	@Override
    public DecayPredicate fromNbt(NbtCompound nbt) {
		String name = nbt.getString("entry");

		if(name.startsWith("#")) tag = TagKey.of(RegistryKeys.BLOCK, Identifier.tryParse(name.substring(1)));
		else block = Registries.BLOCK.get(Identifier.tryParse(name));
        return this;
    }

    @Override
    public NbtCompound toNbt(NbtCompound nbt) {
        DecayPredicate.super.toNbt(nbt);
        nbt.putString("entry", tag != null ? "#" + tag.id().toString() : Registries.BLOCK.getId(block).toString());
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
    public boolean test(World world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
		return block != null ? targetBlock.isOf(block) : targetBlock.isIn(tag);
    }

	@Override
	public Set<Block> constructApplicableBlocks() {
		return block != null ? Set.of(block) : Streams.stream(Registries.BLOCK.iterateEntries(tag)).map(RegistryEntry::value).collect(Collectors.toSet());
	}

	public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Block block;
		private TagKey<Block> tag;

        public Builder block(Block block) {
            this.block = block;
            return this;
        }

		public Builder tag(TagKey<Block> tag) {
			this.tag = tag;
			return this;
		}

        public SimpleDecayPredicate create() {
            return new SimpleDecayPredicate(tag, block);
        }
    }
}
