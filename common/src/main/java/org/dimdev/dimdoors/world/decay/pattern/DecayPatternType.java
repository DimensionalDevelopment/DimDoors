package org.dimdev.dimdoors.world.decay.pattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;

public record DecayPatternType<T extends DecayPattern>(MapCodec<T> codec) {
    public static final Registrar<DecayPatternType<? extends DecayPattern>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<DecayPatternType<? extends DecayPattern>>builder(DimensionalDoors.id("decay_pattern_type")).build();


    public static final Codec<DecayPatternType<? extends DecayPattern>> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);

    public static final RegistrySupplier<DecayPatternType<CompoundDecayPattern>> COMPOUND = register(DimensionalDoors.id(CompoundDecayPattern.KEY), CompoundDecayPattern.CODEC);
    public static final RegistrySupplier<DecayPatternType<PaintingDecayPattern>> PAINTING = register(DimensionalDoors.id(PaintingDecayPattern.KEY), PaintingDecayPattern.CODEC);

    public static void register() {
    }

    static <T extends DecayPattern> RegistrySupplier<DecayPatternType<T>> register(ResourceLocation id, MapCodec<T> codec) {
        return REGISTRY.register(id, () -> new DecayPatternType<>(codec));
    }
}
