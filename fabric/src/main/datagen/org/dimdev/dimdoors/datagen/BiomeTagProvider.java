package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.dimdev.dimdoors.tag.ModBiomeTags;

import java.util.concurrent.CompletableFuture;

public class BiomeTagProvider extends FabricTagProvider<Biome> {
    public BiomeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BIOME, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        tag(ModBiomeTags.TWO_PILLARS).add(Biomes.PLAINS);
        tag(ModBiomeTags.RED_SANDSTONE_PILLARS).add(Biomes.BADLANDS);
        tag(ModBiomeTags.ICE_PILLARS).addOptionalTag(BiomeTags.HAS_IGLOO.location());
        tag(ModBiomeTags.SANDSTONE_PILLARS).add(Biomes.DESERT);
    }
}
