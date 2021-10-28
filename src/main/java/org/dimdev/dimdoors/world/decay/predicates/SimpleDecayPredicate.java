package org.dimdev.dimdoors.world.decay.predicates;

import com.google.gson.JsonObject;
import org.dimdev.dimdoors.world.decay.DecayPredicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Set;

public class SimpleDecayPredicate implements DecayPredicate {
    public static final String KEY = "simple";

    private Block block;

    public SimpleDecayPredicate() {}

    private SimpleDecayPredicate(Block block) {
        this.block = block;
    }

    @Override
    public DecayPredicate fromNbt(NbtCompound nbt) {
        block = Registry.BLOCK.get(Identifier.tryParse(nbt.getString("block")));
        return this;
    }

    @Override
    public NbtCompound toNbt(NbtCompound nbt) {
        DecayPredicate.super.toNbt(nbt);
        nbt.putString("block", Registry.BLOCK.getId(block).toString());
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

        public SimpleDecayPredicate create() {
            return new SimpleDecayPredicate(block);
        }
    }
}
