package org.dimdev.dimdoors.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModNoiseParameters {
    public static ResourceKey<NoiseParameters> STRAND_A = register("strand_a");
    public static ResourceKey<NoiseParameters> STRAND_B = register("strand_b");
    public static ResourceKey<NoiseParameters> TERRAIN = register("terrain");
    public static ResourceKey<NoiseParameters> X_SHIFT = register("x_shift");
    public static ResourceKey<NoiseParameters> Y_SHIFT = register("y_shift");
    public static ResourceKey<NoiseParameters> Z_SHIFT = register("z_shift");

    private static ResourceKey<NoiseParameters> register(String name) {
        return ResourceKey.create(Registries.NOISE, DimensionalDoors.id("limbo/" + name));
    }

    public static void bootstrap(BootstapContext<NoiseParameters> entries) {
        entries.register(STRAND_A, new NoiseParameters(-7, 1, 0.5, 0.25));
        entries.register(STRAND_B, new NoiseParameters(-7, 1, 0.5, 0.25));
        entries.register(TERRAIN, new NoiseParameters(-7, 1, 1, 0.5, 0.375, 0.25));
        entries.register(X_SHIFT, new NoiseParameters(-7, 1, 0.5, 0.5));
        entries.register(Y_SHIFT, new NoiseParameters(-7, 1, 0.75, 0.5, 0.25));
        entries.register(Z_SHIFT, new NoiseParameters(-7, 1, 0.5, 0.5));
    }
}
