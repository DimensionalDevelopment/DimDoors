package org.dimdev.dimdoors.world;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.dimdev.dimdoors.world.limbo.LimboBiome;
import org.dimdev.dimdoors.world.pocketdimension.BlankBiome;

public final class ModBiomes {
    public static final Biome LIMBO = register(new Identifier("dimdoors:limbo"), new LimboBiome());
    public static final Biome WHITE_VOID = register(new Identifier("dimdoors:white_void"), new BlankBiome(true, false));
    public static final Biome BLACK_VOID = register(new Identifier("dimdoors:black_void"), new BlankBiome(false, false));
    public static final Biome DANGEROUS_BLACK_VOID = register(new Identifier("dimdoors:dangerous_black_void"), new BlankBiome(false, true));

    private static Biome register(Identifier id, Biome biome) {
        Registry.register(Registry.BIOME, id, biome);
        return biome;
    }

    public static void init() {

        // just loads the class
    }
}
