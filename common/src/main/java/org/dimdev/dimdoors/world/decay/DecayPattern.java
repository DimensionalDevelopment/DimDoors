package org.dimdev.dimdoors.world.decay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record DecayPattern(List<DecayCondition> conditions, DecayResult result) {
    public static Codec<DecayPattern> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DecayCondition.LIST_CODEC.fieldOf("conditions").forGetter(DecayPattern::conditions),
            DecayResult.CODEC.fieldOf("result").forGetter(DecayPattern::result)
    ).apply(instance, DecayPattern::new));

    public static final Event<EntropyEvent> ENTROPY_EVENT = EventFactory.of(entropyEvents -> (world, pos, entorpy) -> {
        for (EntropyEvent event : entropyEvents) event.entropy(world, pos, entorpy);
    });

    public boolean test(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid, DecaySource source) {
        return conditions.stream().allMatch(condition -> condition.test(world, pos, origin, targetBlock, targetFluid, source));
    }

    public void process(Level world, BlockPos pos, BlockState origin, BlockState targetBlock, FluidState targetFluid, DecaySource source) {
        ENTROPY_EVENT.invoker().entropy(world, pos, result.process(world, pos, origin, targetBlock, targetFluid, source));
    }

    public <T> Stream<ResourceKey<T>> constructApplicable(RegistryAccess access, ResourceKey<Registry<T>> key) {
        return conditions.stream().filter(a -> a instanceof Applicator<?>).map(a -> (Applicator<?>) a).filter(a -> key.equals(a.registry())).flatMap(a -> a.constructApplicable(access)).map(b -> b.cast(key)).filter(Optional::isPresent).map(Optional::get);
    }

//    public Object willBecome(Object prior) {
//        return result.produces(prior);
//    }

    public boolean shouldDropThread(ServerLevel world, BlockPos pos) {
        return world.getRandom().nextFloat() < result.worldThreadChance();
    }

    public interface EntropyEvent {
        void entropy(Level world, BlockPos pos, int entorpy);
    }
}
