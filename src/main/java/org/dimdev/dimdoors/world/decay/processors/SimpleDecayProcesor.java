package org.dimdev.dimdoors.world.decay.processors;

import com.google.gson.JsonObject;
import org.dimdev.dimdoors.world.decay.DecayProcessor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

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
    public DecayProcessor fromJson(JsonObject json) {
        block = Registry.BLOCK.get(Identifier.tryParse(json.get("block").getAsString()));
        entropy = json.get("entropy").getAsInt();
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        DecayProcessor.super.toJson(json);
        json.addProperty("block", Registry.BLOCK.getId(block).toString());
        json.addProperty("entropy", entropy);
        return json;
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
        world.setBlockState(pos, block.getDefaultState());
        return entropy;
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
