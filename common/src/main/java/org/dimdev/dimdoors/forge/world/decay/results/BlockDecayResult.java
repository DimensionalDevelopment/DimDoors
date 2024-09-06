package org.dimdev.dimdoors.forge.world.decay.results;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.forge.world.decay.DecayResult;
import org.dimdev.dimdoors.forge.world.decay.DecayResultType;

public abstract class BlockDecayResult<T extends BlockDecayResult<T>> implements DecayResult {
    public static <T extends BlockDecayResult<T>> Products.P3<RecordCodecBuilder.Mu<T>, Integer, Float, Block> blockDecayCodec(RecordCodecBuilder.Instance<T> instance) {
        return DecayResult.entropyCodec(instance).and(Registry.BLOCK.byNameCodec().fieldOf("block").forGetter(blockDecayResult -> blockDecayResult.block));
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
    public Object produces(Object prior) {
        return new ItemStack(block);
    }
}
