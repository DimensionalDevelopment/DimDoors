package org.dimdev.dimdoors.world.decay.results;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Supplier;

public abstract class BlockDecayResult<T extends BlockDecayResult<T>> implements DecayResult {
    public static <T extends BlockDecayResult<T>> Products.P3<RecordCodecBuilder.Mu<T>, Integer, Float, Block> blockDecayCodec(RecordCodecBuilder.Instance<T> instance) {
        return DecayResult.entropyCodec(instance).and(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(blockDecayResult -> blockDecayResult.block));
    }

    private final float worldThreadChance;
    protected Block block;

    protected int entropy;

    public BlockDecayResult(int entropy, float worldThreadChance, Block block) {
        this.entropy = entropy;
        this.worldThreadChance = worldThreadChance;
        this.block = block;
    }

    @Override
    public int entropy() {
        return entropy;
    }

    @Override
    public float worldThreadChance() {
        return worldThreadChance;
    }

    @Override
    public abstract DecayResultType<T> getType();

    @Override
    public List<Result> produces() {
        return List.of(new Result(block, 1));
    }

    public static SingleBlockDecayResult single(Block block) {
        return single(block, 1);
    }

    public static SingleBlockDecayResult single(Block block, int entropy) {
        return new SingleBlockDecayResult(entropy, 0.0f, block);
    }

    public static SingleBlockDecayResult single(Supplier<? extends Block> block) {
        return single(block, 1);
    }

    public static SingleBlockDecayResult single(Supplier<? extends Block> block, int entropy) {
        return single(block.get(), entropy);
    }


    public static DoubleBlockDecayResult doubly(Block block) {
        return doubly (block, 1);
    }

    public static DoubleBlockDecayResult doubly(Block block, int entropy) {
        return new DoubleBlockDecayResult(entropy, 0.0f, block);
    }

    public static DoubleBlockDecayResult doubly(Supplier<Block> block) {
        return doubly(block, 1);
    }

    public static DoubleBlockDecayResult doubly(Supplier<Block> block, int entropy) {
        return doubly(block.get(), entropy);
    }
}
