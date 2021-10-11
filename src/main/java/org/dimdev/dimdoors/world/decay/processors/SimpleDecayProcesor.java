package org.dimdev.dimdoors.world.decay.processors;

import com.google.gson.JsonObject;
import net.minecraft.state.property.Property;
import org.dimdev.dimdoors.world.decay.DecayProcessor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Set;
import java.util.stream.Collectors;

public class SimpleDecayProcesor implements DecayProcessor {
    public static final String KEY = "simple";

    private Block block;

    private int entropy;

    public SimpleDecayProcesor() {}

    private SimpleDecayProcesor(Block block, int entropy) {
        this.block = block;
        this.entropy = entropy;
    }

    @Override
    public DecayProcessor fromNbt(NbtCompound json) {
        block = Registry.BLOCK.get(Identifier.tryParse(json.getString("block")));
        entropy = json.getInt("entropy");
        return this;
    }

    @Override
    public NbtCompound toNbt(NbtCompound nbt) {
        DecayProcessor.super.toNbt(nbt);
        nbt.putString("block", Registry.BLOCK.getId(block).toString());
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
    public int process(World world, BlockPos pos, BlockState origin, BlockState target) {
    	BlockState newState = block.getDefaultState();
    	Set<Property<?>> commonProperties = target.getProperties().stream().filter(newState.getProperties()::contains).collect(Collectors.toSet());
    	for(Property<?> property : commonProperties) {
    		newState = transferProperty(target, newState, property);
		}
        world.setBlockState(pos, newState);
        return entropy;
    }

	private static <T extends Comparable<T>> BlockState transferProperty(BlockState from, BlockState to, Property<T> property) {
		return to.with(property, from.get(property));
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
