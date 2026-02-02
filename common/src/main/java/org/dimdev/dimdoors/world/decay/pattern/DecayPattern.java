package org.dimdev.dimdoors.world.decay.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecaySource;
import org.dimdev.dimdoors.world.decay.conditions.Applicator;
import org.dimdev.dimdoors.world.decay.conditions.DecayCondition;
import org.dimdev.dimdoors.world.decay.results.DecayResult;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface DecayPattern {
    Codec<DecayPattern> CODEC = DecayPatternType.CODEC.dispatch("type", DecayPattern::getType, DecayPatternType::codec);

    DecayPatternType<? extends DecayPattern> getType();

    Event<EntropyEvent> ENTROPY_EVENT = EventFactory.of(entropyEvents -> (world, pos, entorpy) -> {
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
