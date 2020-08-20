package org.dimdev.dimdoors.world;

import org.dimdev.dimdoors.world.limbo.LimboBiomeSource;
import org.dimdev.dimdoors.world.limbo.LimboChunkGenerator;
import org.dimdev.dimdoors.world.pocket.BlankChunkGenerator;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public final class ModDimensions {
    public static final RegistryKey<World> LIMBO = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:limbo"));
    public static final RegistryKey<World> PERSONAL = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:personal_pockets"));
    public static final RegistryKey<World> PUBLIC = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:public_pockets"));
    public static final RegistryKey<World> DUNGEON = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:dungeon_pockets"));

    public static final RegistryKey<DimensionType> LIMBO_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("dimdoors:limbo"));
    public static final RegistryKey<DimensionType> POCKET_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("dimdoors:personal_pockets"));

    public static boolean isDimDoorsPocketDimension(World world) {
        RegistryKey<World> type = world.getRegistryKey();
        return type == PERSONAL || type == PUBLIC || type == DUNGEON;
    }

    public static boolean isLimboDimension(World world) {
        return world.getRegistryKey() == LIMBO;
    }

    public static void init() {
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier("dimdoors", "blank"), BlankChunkGenerator.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier("dimdoors", "limbo_chunk_generator"), LimboChunkGenerator.CODEC);
        Registry.register(Registry.BIOME_SOURCE, new Identifier("dimdoors", "limbo_biome_source"), LimboBiomeSource.CODEC);
    }
}
