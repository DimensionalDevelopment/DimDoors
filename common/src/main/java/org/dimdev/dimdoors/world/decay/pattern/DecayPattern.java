package org.dimdev.dimdoors.world.decay.pattern;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.util.SimpleEvent;
import org.dimdev.dimdoors.world.decay.Decay;

import java.util.stream.Stream;

public interface DecayPattern {
    Codec<DecayPattern> CODEC = DecayPatternType.CODEC.dispatch(DecayPattern::getType, DecayPatternType::codec);

    DecayPatternType<? extends DecayPattern> getType();

    SimpleEvent<EntropyEvent> ENTROPY_EVENT = SimpleEvent.of(entropyEvents -> (world, pos, entorpy) -> {
        for (EntropyEvent event : entropyEvents) event.entropy(world, pos, entorpy);
    });

    boolean test(Decay.DecayContext context);

    int process(Decay.DecayContext context);

    Stream<ResourceKey<?>> constructApplicable(RegistryAccess access);

    default void applyPattern(Decay.DecayContext context) {
        ENTROPY_EVENT.invoker().entropy(context.world(), context.targetBlockPos(), process(context));
    }

    interface EntropyEvent {
        void entropy(Level world, BlockPos pos, int entorpy);
    }

    public interface Builder<T extends DecayPattern> {
        public T build(HolderLookup.Provider provider);
    }
}
