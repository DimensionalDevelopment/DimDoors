package org.dimdev.dimdoors.world.decay.predicates;

import com.google.common.collect.Streams;
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
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayPredicate;

import java.util.Set;
import java.util.stream.Collectors;

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
    public DecayPredicate fromNbt(CompoundTag nbt) {
		String name = nbt.getString("entry");

		if(name.startsWith("#")) tag = TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(name.substring(1)));
		else block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(name));
        return this;
    }

    @Override
    public CompoundTag toNbt(CompoundTag nbt) {
        DecayPredicate.super.toNbt(nbt);
        nbt.putString("entry", tag != null ? "#" + tag.location().toString() : BuiltInRegistries.BLOCK.getKey(block).toString());
        return nbt;
    }

    @Override
    public DecayPredicateType<? extends DecayPredicate> getType() {
        return DecayPredicateType.SIMPLE_PREDICATE_TYPE.get();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid) {
		return block != null ? targetBlock.is(block) : targetBlock.is(tag);
    }

	@Override
	public Set<Block> constructApplicableBlocks() {
		return block != null ? Set.of(block) : Streams.stream(BuiltInRegistries.BLOCK.getTagOrEmpty(tag)).map(Holder::value).collect(Collectors.toSet());
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
