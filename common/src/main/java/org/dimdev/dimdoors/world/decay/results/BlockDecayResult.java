package org.dimdev.dimdoors.world.decay.results;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.api.util.LocationValue;
import org.dimdev.dimdoors.world.decay.DecayResult;
import org.dimdev.dimdoors.world.decay.DecayResultType;

public abstract class BlockDecayResult<T extends BlockDecayResult<T>> implements DecayResult {
    public static <T extends BlockDecayResult<T>> Products.P3<RecordCodecBuilder.Mu<T>, Integer, LocationValue, Block> blockDecayCodec(RecordCodecBuilder.Instance<T> instance) {
        return DecayResult.entropyCodec(instance).and(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(blockDecayResult -> blockDecayResult.block));
    }

    private final LocationValue worldThreadChance;
    protected Block block;

    protected int entropy;

    public BlockDecayResult(int entropy, LocationValue worldThreadChance, Block block) {
        this.entropy = entropy;
        this.worldThreadChance = worldThreadChance;
        this.block = block;
    }

    @Override
    public int entropy() {
        return entropy;
    }

    @Override
    public LocationValue worldThreadChance() {
        return worldThreadChance;
    }

    @Override
    public abstract DecayResultType<T> getType();

    @Override
    public Object produces(Object prior) {
        return new ItemStack(block);
    }
}
