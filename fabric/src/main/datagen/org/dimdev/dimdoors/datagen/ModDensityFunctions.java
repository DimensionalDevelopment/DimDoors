package org.dimdev.dimdoors.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModDensityFunctions {
    public static ResourceKey<DensityFunction> FINAL_DENSITY = register("limbo/final_density");
    public static ResourceKey<DensityFunction> STRAND = register("limbo/strand");
    public static ResourceKey<DensityFunction> TERRAIN = register("limbo/terrain");
    public static ResourceKey<DensityFunction> X_SHIFT = register("limbo/x_shift");
    public static ResourceKey<DensityFunction> Y_SHIFT = register("limbo/y_shift");
    public static ResourceKey<DensityFunction> Z_SHIFT = register("limbo/z_shift");


    public static ResourceKey<DensityFunction> register(String name) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, DimensionalDoors.id(name));
    }

    public static void bootstrap(BootstapContext<DensityFunction> entries) {
        var parameters = entries.lookup(Registries.NOISE);
        var functions = entries.lookup(Registries.DENSITY_FUNCTION);

        var shift_x = DensityFunctions.mul(DensityFunctions.constant(75), DensityFunctions.noise(parameters.getOrThrow(ModNoiseParameters.X_SHIFT), 4, 0.675));
        entries.register(X_SHIFT, shift_x);
        var shift_y = DensityFunctions.mul(DensityFunctions.constant(125), DensityFunctions.noise(parameters.getOrThrow(ModNoiseParameters.Y_SHIFT), 2, 2));
        entries.register(Y_SHIFT, shift_y);
        var shift_z = DensityFunctions.mul(DensityFunctions.constant(75), DensityFunctions.noise(parameters.getOrThrow(ModNoiseParameters.X_SHIFT), 4, 0.675));
        entries.register(Z_SHIFT, shift_z);

        var terrain = DensityFunctions.add(
                DensityFunctions.yClampedGradient(0, 256, 0.32, -0.35),
                DensityFunctions.shiftedNoise2d(shift_x/*, shift_y.value()*/, shift_z, 2/*, 9.75*/, parameters.getOrThrow(ModNoiseParameters.TERRAIN)));
        entries.register(TERRAIN, terrain);


        var noodle_function = parameters.getOrThrow(ResourceKey.create(Registries.NOISE, new ResourceLocation("minecraft:noodle")));
        var thick_noodle_noise = parameters.getOrThrow(ResourceKey.create(Registries.NOISE, new ResourceLocation("minecraft:noodle_thickness")));
        var y_function =  new DensityFunctions.HolderHolder(functions.getOrThrow(ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation("minecraft:y"))));

        var strand = DensityFunctions.add(
                DensityFunctions.mul(DensityFunctions.interpolated(terrain), DensityFunctions.constant(0.175)),
                DensityFunctions.mul(
                        DensityFunctions.constant(-1),
                        DensityFunctions.rangeChoice(
                                DensityFunctions.interpolated(
                                        DensityFunctions.rangeChoice(
                                                y_function,
                                                -60,
                                                255,
                                                DensityFunctions.noise(noodle_function, 0.75, 0.75),
                                                DensityFunctions.constant(-1)
                                        )
                                ),
                                -1000000,
                                0,
                                DensityFunctions.constant(64),
                                DensityFunctions.add(
                                        DensityFunctions.interpolated(
                                DensityFunctions.rangeChoice(
                                        y_function,
                                        -60,
                                        255,
                                        DensityFunctions.add(
                                                DensityFunctions.constant(-0.075),
                                                DensityFunctions.mul(
                                                        DensityFunctions.constant(-0.065),
                                                        DensityFunctions.noise(thick_noodle_noise, 1, 1)
                                                )
                                        ),
                                        DensityFunctions.constant(0)
                                )
                        ), DensityFunctions.mul(
                                DensityFunctions.constant(1.5),
                                                DensityFunctions.max(
                                                        DensityFunctions.interpolated(
                                                                DensityFunctions.rangeChoice(
                                                                        y_function,
                                                                        -60,
                                                                        255,
                                                                        DensityFunctions.noise(parameters.getOrThrow(ModNoiseParameters.STRAND_A), 2, 1.33),
                                                                        DensityFunctions.constant(0)
                                                                )).abs(),
                                                        DensityFunctions.interpolated(
                                                                DensityFunctions.rangeChoice(
                                                                        y_function,
                                                                        -60,
                                                                        255,
                                                                        DensityFunctions.noise(parameters.getOrThrow(ModNoiseParameters.STRAND_B), 2.25, 1.125),
                                                                        DensityFunctions.constant(0)
                                                                )).abs())
                                                )
                                        )
                        )
                )
        );
        entries.register(STRAND, strand);

//        entries.add(FINAL_DENSITY, max(
//                "",
//                DensityFunctions.09oi/lk
//        ))
    }

    private static ResourceKey<DensityFunction> createKey(String location) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation(location));
    }
}