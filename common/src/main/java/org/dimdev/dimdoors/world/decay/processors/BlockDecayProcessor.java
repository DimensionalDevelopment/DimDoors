package org.dimdev.dimdoors.world.decay.processors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.decay.DecayProcessorType;

import java.util.Set;
import java.util.stream.Collectors;

public class BlockDecayProcessor implements DecayProcessor<Block, ItemStack> {
    public static final String KEY = "block";

    protected Block block;

    protected int entropy;

    public BlockDecayProcessor() {}

    protected BlockDecayProcessor(Block block, int entropy) {
        this.block = block;
        this.entropy = entropy;
    }

    @Override
    public BlockDecayProcessor fromNbt(CompoundTag json) {
        block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(json.getString("block")));
        entropy = json.getInt("entropy");
        return this;
    }

    @Override
    public CompoundTag toNbt(CompoundTag nbt) {
        DecayProcessor.super.toNbt(nbt);
        nbt.putString("block", BuiltInRegistries.BLOCK.getKey(block).toString());
        nbt.putInt("entropy", entropy);
        return nbt;
    }

    @Override
    public DecayProcessorType<BlockDecayProcessor> getType() {
        return DecayProcessorType.SIMPLE_PROCESSOR_TYPE.get();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public int process(Level world, BlockPos pos, BlockState origin, BlockState target, FluidState targetFluid) {
    	BlockState newState = block.defaultBlockState();

		if(target.getBlock() instanceof DoublePlantBlock) pos = target.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.above() : pos;

    	Set<Property<?>> commonProperties = target.getProperties().stream().filter(newState.getProperties()::contains).collect(Collectors.toSet());
    	for(Property<?> property : commonProperties) {
    		newState = transferProperty(target, newState, property);
		}
        world.setBlockAndUpdate(pos, newState);
        return entropy;
    }

    @Override
    public Object produces(Object prior) {
        return new ItemStack(block);
    }

    private static <T extends Comparable<T>> BlockState transferProperty(BlockState from, BlockState to, Property<T> property) {
		return to.setValue(property, from.getValue(property));
	}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Block block = Blocks.AIR;
        private int entropy;

        public Builder block(Block block) {
            this.block = block;
            return this;
        }

        public Builder entropy(int entropy) {
            this.entropy = entropy;
            return this;
        }

        public BlockDecayProcessor create() {
            return new BlockDecayProcessor(block, entropy);
        }
    }
}
