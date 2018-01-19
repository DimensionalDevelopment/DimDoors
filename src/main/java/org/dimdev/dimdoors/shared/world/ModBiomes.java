package org.dimdev.dimdoors.shared.world;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.world.limbo.BiomeLimbo;
import org.dimdev.dimdoors.shared.world.pocketdimension.BiomeBlank;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ModBiomes {

    public static final BiomeLimbo LIMBO = new BiomeLimbo();
    public static final BiomeBlank WHITE_VOID = new BiomeBlank(true, false);
    public static final BiomeBlank BLACK_VOID = new BiomeBlank(false, false);
    public static final BiomeBlank DANGEROUS_BLACK_VOID = new BiomeBlank(false, true);

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        LIMBO.setRegistryName(new ResourceLocation(DimDoors.MODID, "limbo"));
        WHITE_VOID.setRegistryName(new ResourceLocation(DimDoors.MODID, "white_void"));
        BLACK_VOID.setRegistryName(new ResourceLocation(DimDoors.MODID, "black_void"));
        DANGEROUS_BLACK_VOID.setRegistryName(new ResourceLocation(DimDoors.MODID, "dangerous_black_void"));
        event.getRegistry().registerAll(
                LIMBO,
                WHITE_VOID,
                BLACK_VOID,
                DANGEROUS_BLACK_VOID);
        BiomeDictionary.addTypes(LIMBO, BiomeDictionary.Type.VOID); // TODO: check that this prevents other mods' worldgen (ex. Biomes O' Plenty)
        BiomeDictionary.addTypes(WHITE_VOID, BiomeDictionary.Type.VOID);
        BiomeDictionary.addTypes(BLACK_VOID, BiomeDictionary.Type.VOID);
        BiomeDictionary.addTypes(DANGEROUS_BLACK_VOID, BiomeDictionary.Type.VOID);
    }
}
