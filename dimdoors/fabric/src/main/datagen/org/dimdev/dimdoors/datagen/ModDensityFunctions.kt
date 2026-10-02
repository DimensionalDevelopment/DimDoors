package org.dimdev.dimdoors.datagen

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.DensityFunction
import net.minecraft.world.level.levelgen.DensityFunctions
import org.dimdev.dimdoors.api.util.key

object ModDensityFunctions {
    @JvmField val FINAL_DENSITY: ResourceKey<DensityFunction> = register("limbo/final_density")
    @JvmField val STRAND: ResourceKey<DensityFunction> = register("limbo/strand")
    @JvmField val TERRAIN: ResourceKey<DensityFunction> = register("limbo/terrain")
    @JvmField val X_SHIFT: ResourceKey<DensityFunction> = register("limbo/x_shift")
    @JvmField val Y_SHIFT: ResourceKey<DensityFunction> = register("limbo/y_shift")
    @JvmField val Z_SHIFT: ResourceKey<DensityFunction> = register("limbo/z_shift")


    fun register(name: String): ResourceKey<DensityFunction> = Registries.DENSITY_FUNCTION.key(name)

    fun bootstrap(entries: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        val parameters = entries.registrylookup(Registries.NOISE)
        val functions = entries.registrylookup(Registries.DENSITY_FUNCTION)

        val shift_x = DensityFunctions.mul(
            DensityFunctions.constant(75.0),
            DensityFunctions.noise(
                parameters.getOrThrow(ModNoiseParameters.X_SHIFT),
                4.0,
                0.675)
        )
        entries.register(X_SHIFT, shift_x)
        val shift_y = DensityFunctions.mul(DensityFunctions.constant(125.0), DensityFunctions.noise(parameters.getOrThrow(ModNoiseParameters.Y_SHIFT), 2.0, 2.0))
        entries.register(Y_SHIFT, shift_y)
        val shift_z = DensityFunctions.mul(DensityFunctions.constant(75.0), DensityFunctions.noise(parameters.getOrThrow(ModNoiseParameters.Z_SHIFT), 4.0, 0.675))
        entries.register(Z_SHIFT, shift_z)

        val terrain = DensityFunctions.add(
            DensityFunctions.yClampedGradient(0, 256, 0.32, -0.35),
            DensityFunctions.ShiftedNoise(
                DensityFunctions.HolderHolder(functions.getOrThrow(X_SHIFT)),
                DensityFunctions.HolderHolder(functions.getOrThrow(Y_SHIFT)),
                DensityFunctions.HolderHolder(functions.getOrThrow(Z_SHIFT)), 2.0, 9.75,
                DensityFunction.NoiseHolder(parameters.getOrThrow(ModNoiseParameters.TERRAIN))))
        entries.register(TERRAIN, terrain)
        val terrainReference = DensityFunctions.HolderHolder(functions.getOrThrow(TERRAIN))


        val noodle_function = parameters.getOrThrow(Registries.NOISE.key(ResourceLocation.withDefaultNamespace("noodle")))
        val thick_noodle_noise = parameters.getOrThrow(Registries.NOISE.key(ResourceLocation.withDefaultNamespace("noodle_thickness")))
        val y_function = DensityFunctions.HolderHolder(functions.getOrThrow(Registries.DENSITY_FUNCTION.key(ResourceLocation.withDefaultNamespace("y"))))

        val strand = DensityFunctions.add(
            DensityFunctions.mul(DensityFunctions.interpolated(terrain), DensityFunctions.constant(0.175)),
            DensityFunctions.mul(
                DensityFunctions.constant(-1.0),
                DensityFunctions.rangeChoice(
                    DensityFunctions.interpolated(
                        DensityFunctions.rangeChoice(
                            y_function,
                            -60.0,
                            255.0,
                            DensityFunctions.noise(noodle_function, 0.75, 0.75),
                            DensityFunctions.constant(-1.0)
                        )
                    ),
                    -1000000.0,
                    0.0,
                    DensityFunctions.constant(64.0),
                    DensityFunctions.add(
                        DensityFunctions.interpolated(
                            DensityFunctions.rangeChoice(
                                y_function,
                                -60.0,
                                255.0,
                                DensityFunctions.add(
                                    DensityFunctions.constant(-0.075),
                                    DensityFunctions.mul(
                                        DensityFunctions.constant(-0.065),
                                        DensityFunctions.noise(thick_noodle_noise, 1.0, 1.0)
                                    )
                                ),
                                DensityFunctions.constant(0.0)
                            )
                        ), DensityFunctions.mul(
                            DensityFunctions.constant(1.5),
                            DensityFunctions.max(
                                DensityFunctions.interpolated(
                                    DensityFunctions.rangeChoice(
                                        y_function,
                                        -60.0,
                                        255.0,
                                        DensityFunctions.noise(parameters.getOrThrow(ModNoiseParameters.STRAND_A), 2.0, 1.33),
                                        DensityFunctions.constant(0.0)
                                    )).abs(),
                                DensityFunctions.interpolated(
                                    DensityFunctions.rangeChoice(
                                        y_function,
                                        -60.0,
                                        255.0,
                                        DensityFunctions.noise(parameters.getOrThrow(ModNoiseParameters.STRAND_B), 2.25, 1.125),
                                        DensityFunctions.constant(0.0)
                                    )).abs())
                        )
                    )
                )
            )
        )
        entries.register(STRAND, strand)

        entries.register(FINAL_DENSITY,
            DensityFunctions.max(
                DensityFunctions.mul(
                    DensityFunctions.constant(0.64),
                    DensityFunctions.interpolated(
                        DensityFunctions.blendDensity(
                            DensityFunctions.add(
                                DensityFunctions.mul(
                                    DensityFunctions.yClampedGradient(-56, -32, 0.0, 1.0),
                                    DensityFunctions.add(
                                        DensityFunctions.add(
                                            DensityFunctions.mul(
                                                DensityFunctions.yClampedGradient(240, 256, 1.0, 0.0),
                                                DensityFunctions.add(
                                                    DensityFunctions.max(
                                                        DensityFunctions.constant(-1.0),
                                                        terrainReference
                                                    ),
                                                    DensityFunctions.constant(0.078125)
                                                )
                                            ),
                                            DensityFunctions.constant(-0.078125)
                                        ),
                                        DensityFunctions.constant(-0.1171875)
                                    )
                                ),
                                DensityFunctions.constant(0.1171875)
                            )
                        )
                    )
                ).squeeze(),
                DensityFunctions.HolderHolder(functions.getOrThrow(STRAND))
            ))

//        ))
    }

    private fun createKey(location: String): ResourceKey<DensityFunction> = Registries.DENSITY_FUNCTION.key(ResourceLocation.parse(location))
}
