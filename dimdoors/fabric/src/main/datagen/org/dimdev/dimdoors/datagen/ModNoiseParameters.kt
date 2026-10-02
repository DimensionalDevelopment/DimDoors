package org.dimdev.dimdoors.datagen

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters
import org.dimdev.dimdoors.api.util.key

object ModNoiseParameters {
    @JvmField val STRAND_A: ResourceKey<NoiseParameters> = register("strand_a")
    @JvmField val STRAND_B: ResourceKey<NoiseParameters> = register("strand_b")
    @JvmField val TERRAIN: ResourceKey<NoiseParameters> = register("terrain")
    @JvmField val X_SHIFT: ResourceKey<NoiseParameters> = register("x_shift")
    @JvmField val Y_SHIFT: ResourceKey<NoiseParameters> = register("y_shift")
    @JvmField val Z_SHIFT: ResourceKey<NoiseParameters> = register("z_shift")

    private fun register(name: String): ResourceKey<NoiseParameters> = Registries.NOISE.key("limbo/$name")

    fun bootstrap(entries: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        entries.register(STRAND_A, NoiseParameters(-7, 1.0, 0.5, 0.25))
        entries.register(STRAND_B, NoiseParameters(-7, 1.0, 0.5, 0.25))
        entries.register(TERRAIN, NoiseParameters(-7, 1.0, 1.0, 0.5, 0.375, 0.25))
        entries.register(X_SHIFT, NoiseParameters(-7, 1.0, 0.5, 0.5))
        entries.register(Y_SHIFT, NoiseParameters(-7, 1.0, 0.75, 0.5, 0.25))
        entries.register(Z_SHIFT, NoiseParameters(-7, 1.0, 0.5, 0.5))
    }
}
