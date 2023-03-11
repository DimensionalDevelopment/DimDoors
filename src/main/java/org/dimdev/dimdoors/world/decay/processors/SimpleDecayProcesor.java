package org.dimdev.dimdoors.world.decay.processors;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import org.dimdev.dimdoors.world.decay.DecayProcessor;

public class SimpleDecayProcesor implements DecayProcessor {
    public static final String KEY = "simple";

    protected Block block;

    protected int entropy;

    public SimpleDecayProcesor() {}

    protected SimpleDecayProcesor(Block block, int entropy) {
        this.block = block;
        this.entropy = entropy;
    }

    @Override
    public DecayProcessor fromNbt(CompoundTag json) {
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
    public DecayProcessorType<? extends DecayProcessor> getType() {
        return DecayProcessorType.SIMPLE_PROCESSOR_TYPE;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public int process(Level world, BlockPos pos, BlockState origin, BlockState target) {
    	BlockState newState = block.defaultBlockState();

		if(target.getBlock() instanceof DoublePlantBlock) pos = target.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;

    	Set<Property<?>> commonProperties = target.getProperties().stream().filter(newState.getProperties()::contains).collect(Collectors.toSet());
    	for(Property<?> property : commonProperties) {
    		newState = transferProperty(target, newState, property);
		}
        world.setBlockAndUpdate(pos, newState);
        return entropy;
    }

	private static <T extends Comparable<T>> BlockState transferProperty(BlockState from, BlockState to, Property<T> property) {
		return to.setValue(property, from.getValue(property));
	}

    public static SimpleDecayProcesor.Builder builder() {
        return new SimpleDecayProcesor.Builder();
    }

    public static class Builder {
        private Block block = Blocks.AIR;
        private int entropy;

        public SimpleDecayProcesor.Builder block(Block block) {
            this.block = block;
            return this;
        }

        public SimpleDecayProcesor.Builder entropy(int entropy) {
            this.entropy = entropy;
            return this;
        }

        public SimpleDecayProcesor create() {
            return new SimpleDecayProcesor(block, entropy);
        }
    }
}
