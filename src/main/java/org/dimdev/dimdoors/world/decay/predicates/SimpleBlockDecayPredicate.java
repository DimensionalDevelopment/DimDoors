package org.dimdev.dimdoors.world.decay.predicates;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.world.decay.DecayPredicate;

import java.util.Set;

public class SimpleBlockDecayPredicate implements DecayPredicate {
    public static final String KEY = "simple_block";

    private Block block;

    public SimpleBlockDecayPredicate() {}

    private SimpleBlockDecayPredicate(Block block) {
        this.block = block;
    }

    @Override
    public DecayPredicate fromNbt(NbtCompound nbt) {
        block = Registries.BLOCK.get(Identifier.tryParse(nbt.getString("block")));
        return this;
    }

    @Override
    public NbtCompound toNbt(NbtCompound nbt) {
        DecayPredicate.super.toNbt(nbt);
        nbt.putString("block", Registries.BLOCK.getId(block).toString());
        return nbt;
    }

    @Override
    public DecayPredicateType<? extends DecayPredicate> getType() {
        return DecayPredicateType.SIMPLE_BLOCK_PREDICATE_TYPE;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public boolean test(World world, BlockPos pos, BlockState origin, BlockState target) {
        BlockState state = world.getBlockState(pos);

        return state.getBlock() == block;
    }

	@Override
	public Set<Block> constructApplicableBlocks() {
		return Set.of(block);
	}

	public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Block block = Blocks.AIR;

        public Builder block(Block block) {
            this.block = block;
            return this;
        }

        public SimpleBlockDecayPredicate create() {
            return new SimpleBlockDecayPredicate(block);
        }
    }
}
