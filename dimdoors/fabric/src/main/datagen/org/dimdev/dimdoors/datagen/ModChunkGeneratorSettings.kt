package org.dimdev.dimdoors.datagen

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.biome.Climate
import net.minecraft.world.level.biome.Climate.Parameter.point
import net.minecraft.world.level.biome.Climate.Parameter.span
import net.minecraft.world.level.levelgen.DensityFunctions
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.NoiseRouter
import net.minecraft.world.level.levelgen.NoiseSettings
import net.minecraft.world.level.levelgen.SurfaceRules
import net.minecraft.world.level.levelgen.VerticalAnchor
import org.dimdev.dimdoors.api.util.key
import org.dimdev.dimdoors.block.ModBlocks

object ModChunkGeneratorSettings {
    @JvmField val LIMBO: ResourceKey<NoiseGeneratorSettings> = Registries.NOISE_SETTINGS.key("limbo")

    fun bootstrap(context: DimDoorsDynamicRegistryProvider.RegistrationHelper) {
        context.register(LIMBO, NoiseGeneratorSettings(
            NoiseSettings(0, 256, 1, 1),
            ModBlocks.UNRAVELLED_FABRIC.defaultBlockState(),
            ModBlocks.ETERNAL_FLUID.defaultBlockState(),
            NoiseRouter(
                DensityFunctions.constant(0.0),
                DensityFunctions.constant(0.0),
                DensityFunctions.constant(0.0),
                DensityFunctions.noise(context.lookup(Registries.NOISE.key(ResourceLocation.withDefaultNamespace("aquifer_lava"))), 1.0, 1.0),
                DensityFunctions.constant(0.0),
                DensityFunctions.constant(0.0),
                DensityFunctions.constant(0.0),
                DensityFunctions.constant(0.0),
                DensityFunctions.constant(0.0),
                DensityFunctions.constant(0.0),
                DensityFunctions.constant(1.0),
                DensityFunctions.HolderHolder(context.lookup(ModDensityFunctions.FINAL_DENSITY)),
                DensityFunctions.constant(0.0),
                DensityFunctions.constant(0.0),
                DensityFunctions.constant(0.0)
            ),
            SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                    SurfaceRules.verticalGradient("dimdoors:floor",
                    VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(5)),
                    SurfaceRules.state(ModBlocks.BLACK_ANCIENT_FABRIC.defaultBlockState())
                )
            ),
            listOf(Climate.parameters(
                span(-1f, 1f),
                span(-1f, 1f),
                span(-0.11f, 1f),
                span(-1f, 1f),
                point(0f),
                span(-1f, -0.16f),
                0f
            ), Climate.parameters(
                span(-1f, 1f),
                span(-1f, 1f),
                span(-0.11f, 1f),
                span(-1f, 1f),
                point(0f),
                span(0.16f, 1f),
                0f
            )),
            11,
            false,
            false,
            false,
            false
            )

        )
    }
}
