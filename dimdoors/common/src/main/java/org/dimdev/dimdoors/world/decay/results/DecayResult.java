package org.dimdev.dimdoors.world.decay.results;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.world.decay.Decay;

import java.util.List;

public interface DecayResult {
    public static <T extends DecayResult> Products.P2<RecordCodecBuilder.Mu<T>, Integer, Float> entropyCodec(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Codec.INT.optionalFieldOf("entropy", 0).forGetter(DecayResult::entropy),
                Codec.FLOAT.optionalFieldOf("world_thread_chance", 0.1f).forGetter(DecayResult::worldThreadChance));
    }

    Codec<DecayResult> CODEC = DecayResultType.CODEC.dispatch("type", DecayResult::getType, DecayResultType::codec);


    default int entropy() {
        return 0;
    }

    default float worldThreadChance() {
        return 0f;
    }

    DecayResultType<? extends DecayResult> getType();

    int process(Decay.DecayContext context);

    List<Result> produces();

    public record Result(Object obj, int amount) {}
}
