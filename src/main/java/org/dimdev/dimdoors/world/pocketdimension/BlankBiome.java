package org.dimdev.dimdoors.world.pocketdimension;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import org.dimdev.dimdoors.sound.ModSoundEvents;

public class BlankBiome extends Biome {
    public BlankBiome(boolean white, boolean dangerous) {
        super(new Biome.Settings()
                .configureSurfaceBuilder(
                        SurfaceBuilder.DEFAULT,
                        new TernarySurfaceConfig(
                                Blocks.AIR.getDefaultState(),
                                Blocks.AIR.getDefaultState(),
                                Blocks.AIR.getDefaultState()
                        )
                )
                .precipitation(Biome.Precipitation.NONE).category(Biome.Category.NETHER)
                .depth(0)
                .scale(0)
                .temperature(0.8f)
                .downfall(0)
                .effects(createEffect(white))
                .parent(null)
                .noises(ImmutableList.of())
        );
    }

    private static BiomeEffects createEffect(boolean white) {
        BiomeEffects.Builder builder = new BiomeEffects.Builder()
                .waterColor(white ? 0xFFFFFF : 0x000000)
                .waterFogColor(white ? 0xFFFFFF : 0x000000)
                .fogColor(white ? 0xFFFFFF : 0x000000);
        if(white) builder.loopSound(ModSoundEvents.WHITE_VOID);
        return builder.build();
    }
}
