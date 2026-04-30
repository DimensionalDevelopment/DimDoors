package org.dimdev.dimdoors.world.decay.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.DimensionalDoors;

public record DecayPatternType<T extends DecayPattern>(MapCodec<T> codec) {
    public static final ResourceKey<Registry<DecayPatternType<? extends DecayPattern>>> KEY = ResourceKey.createRegistryKey(DimensionalDoors.id("decay_pattern_type"));
    public static final Registry<DecayPatternType<? extends DecayPattern>> REGISTRY = DimensionalDoors.getSided().createRegistry(KEY);


    public static final Codec<DecayPatternType<? extends DecayPattern>> CODEC = REGISTRY.byNameCodec();

    public static final DecayPatternType<CompoundDecayPattern> COMPOUND = register(CompoundDecayPattern.KEY, CompoundDecayPattern.CODEC);
    public static final DecayPatternType<PaintingDecayPattern> PAINTING = register(PaintingDecayPattern.KEY, PaintingDecayPattern.CODEC);

    public static void register() {
    }

    static <T extends DecayPattern> DecayPatternType<T> register(String id, MapCodec<T> codec) {
        return DimensionalDoors.getSided().register(KEY, id, new DecayPatternType<>(codec));
    }
}
