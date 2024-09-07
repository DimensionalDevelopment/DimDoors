package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.dimdev.dimdoors.tag.ModBiomeTags;

public class BiomeTagProvider extends FabricTagProvider.DynamicRegistryTagProvider<Biome> {
    public BiomeTagProvider(FabricDataGenerator output) {
        super(output, Registry.BIOME_REGISTRY);
    }

    @Override
    protected void generateTags() {

        tag(ModBiomeTags.ENCLOSED_RED_SANDSTONE_GATEWAY).add(
                Biomes.BADLANDS,
                Biomes.ERODED_BADLANDS,
                Biomes.WOODED_BADLANDS
        );
        tag(ModBiomeTags.ENCLOSED_QUARTZ_GATEWAY).add(
                Biomes.SNOWY_PLAINS,
                Biomes.SNOWY_SLOPES,
                Biomes.FROZEN_PEAKS,
                Biomes.SNOWY_BEACH,
                Biomes.SNOWY_TAIGA
        );
        tag(ModBiomeTags.ENCLOSED_SANDSTONE_GATEWAY).add(Biomes.DESERT);
        tag(ModBiomeTags.ENCLOSED_MUD_GATEWAY).add(
                Biomes.SWAMP,
                Biomes.MANGROVE_SWAMP
        );
        tag(ModBiomeTags.ENCLOSED_PRISMARINE_GATEWAY).add(
                Biomes.WARM_OCEAN,
                Biomes.LUKEWARM_OCEAN,
                Biomes.DEEP_LUKEWARM_OCEAN,
                Biomes.OCEAN,
                Biomes.DEEP_OCEAN,
                Biomes.COLD_OCEAN,
                Biomes.DEEP_COLD_OCEAN,
                Biomes.FROZEN_OCEAN,
                Biomes.DEEP_FROZEN_OCEAN
        );
        tag(ModBiomeTags.ENCLOSED_ENDSTONE_GATEWAY).add(
                Biomes.THE_END,
                Biomes.END_HIGHLANDS,
                Biomes.END_MIDLANDS,
                Biomes.SMALL_END_ISLANDS,
                Biomes.END_BARRENS
        );
        tag(ModBiomeTags.ENCLOSED_GATEWAY).add(
                Biomes.PLAINS,
                Biomes.SUNFLOWER_PLAINS,
                Biomes.FOREST,
                Biomes.FLOWER_FOREST,
                Biomes.BIRCH_FOREST,
                Biomes.DARK_FOREST,
                Biomes.OLD_GROWTH_BIRCH_FOREST,
                Biomes.OLD_GROWTH_PINE_TAIGA,
                Biomes.OLD_GROWTH_SPRUCE_TAIGA,
                Biomes.TAIGA,
                Biomes.SAVANNA,
                Biomes.SAVANNA_PLATEAU,
                Biomes.WINDSWEPT_HILLS,
                Biomes.WINDSWEPT_GRAVELLY_HILLS,
                Biomes.WINDSWEPT_FOREST,
                Biomes.WINDSWEPT_SAVANNA,
                Biomes.MEADOW,
                Biomes.GROVE,
                Biomes.JAGGED_PEAKS,
                Biomes.STONY_PEAKS,
                Biomes.BEACH,
                Biomes.STONY_SHORE,
                Biomes.MUSHROOM_FIELDS,
                Biomes.GROVE
        );
//        tag(ModBiomeTags.LIMBO_GATEWAY).add(ModBiomes.LIMBO_KEY);


    }
}
