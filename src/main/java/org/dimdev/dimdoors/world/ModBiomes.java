package org.dimdev.dimdoors.world;

import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dimdev.dimdoors.world.limbo.LimboBiome;
import org.dimdev.dimdoors.world.pocketdimension.BlankBiome;

public final class ModBiomes {
    public static final LimboBiome LIMBO = new LimboBiome();
    public static final BlankBiome WHITE_VOID = new BlankBiome(true, false);
    public static final BlankBiome BLACK_VOID = new BlankBiome(false, false);
    public static final BlankBiome DANGEROUS_BLACK_VOID = new BlankBiome(false, true);

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        LIMBO.setRegistryName(new Identifier("dimdoors", "limbo"));
        WHITE_VOID.setRegistryName(new Identifier("dimdoors", "white_void"));
        BLACK_VOID.setRegistryName(new Identifier("dimdoors", "black_void"));
        DANGEROUS_BLACK_VOID.setRegistryName(new Identifier("dimdoors", "dangerous_black_void"));
        event.getRegistry().registerAll(
                LIMBO,
                WHITE_VOID,
                BLACK_VOID,
                DANGEROUS_BLACK_VOID);
        BiomeDictionary.addTypes(LIMBO, BiomeDictionary.Type.VOID);
        BiomeDictionary.addTypes(WHITE_VOID, BiomeDictionary.Type.VOID);
        BiomeDictionary.addTypes(BLACK_VOID, BiomeDictionary.Type.VOID);
        BiomeDictionary.addTypes(DANGEROUS_BLACK_VOID, BiomeDictionary.Type.VOID);
    }
}
