package org.dimdev.dimdoors.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.*;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;

import java.util.List;

import static net.minecraft.world.level.biome.Climate.Parameter.point;
import static net.minecraft.world.level.biome.Climate.Parameter.span;

public class ModChunkGeneratorSettings {
    public static final ResourceKey<NoiseGeneratorSettings> LIMBO = ResourceKey.create(Registries.NOISE_SETTINGS, DimensionalDoors.id("limbo"));

    public static void bootstrap(BootstapContext<NoiseGeneratorSettings> context) {
        context.register(LIMBO, new NoiseGeneratorSettings(
                new NoiseSettings(0, 256, 1, 1),
                ModBlocks.UNRAVELLED_FABRIC.get().defaultBlockState(),
                ModBlocks.ETERNAL_FLUID.get().defaultBlockState(),
                new NoiseRouter(
                        DensityFunctions.constant(0),
                        DensityFunctions.constant(0),
                        DensityFunctions.constant(0),
                        DensityFunctions.noise(context.lookup(Registries.NOISE).getOrThrow(ResourceKey.create(Registries.NOISE, new ResourceLocation("minecraft:aquifer_lava"))), 1, 1),
                        DensityFunctions.constant(0),
                        DensityFunctions.constant(0),
                        DensityFunctions.constant(0),
                        DensityFunctions.constant(0),
                        DensityFunctions.constant(0),
                        DensityFunctions.constant(0),
                        DensityFunctions.constant(1),
                        new DensityFunctions.HolderHolder(context.lookup(Registries.DENSITY_FUNCTION).getOrThrow(ModDensityFunctions.FINAL_DENSITY)),
                        DensityFunctions.constant(0),
                        DensityFunctions.constant(0),
                        DensityFunctions.constant(0)
                ),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(
                                SurfaceRules.verticalGradient("dimdoors:floor",
                                VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(5)),
                                SurfaceRules.state(ModBlocks.BLACK_ANCIENT_FABRIC.get().defaultBlockState())
                        )
                ),
                List.of(Climate.parameters(
                        span(-1, 1),
                        span(-1, 1),
                        span(-0.11f, 1),
                        span(-1, 1),
                        point(0),
                        span(-1, -0.16f),
                        0
                ), Climate.parameters(
                        span(-1, 1),
                        span(-1, 1),
                        span(-0.11f, 1),
                        span(-1, 1),
                        point(0),
                        span(0.16f, 1),
                        0
                )),
                11,
                false,
                false,
                false,
                false
                )

        );
    }
}
