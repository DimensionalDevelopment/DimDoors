package org.dimdev.dimdoors.world;

import org.dimdev.dimdoors.world.limbo.LimboBiomeSource;
import org.dimdev.dimdoors.world.limbo.LimboChunkGenerator;
import org.dimdev.dimdoors.world.pocket.BlankChunkGenerator;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class ModDimensions {
    public static final RegistryKey<World> LIMBO = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:limbo"));
    public static final RegistryKey<World> PERSONAL = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:personal_pockets"));
    public static final RegistryKey<World> PUBLIC = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:public_pockets"));
    public static final RegistryKey<World> DUNGEON = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:dungeon_pockets"));

    public static final RegistryKey<DimensionType> LIMBO_TYPE = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("dimdoors:limbo"));
    public static final RegistryKey<DimensionType> POCKET_TYPE = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("dimdoors:personal_pockets"));

    public static DimensionType limboDimensionType;
    public static DimensionType pocketDimensionType;

    public static ServerWorld limboDimension;
    public static ServerWorld personalPocketDimension;
    public static ServerWorld publicPocketDimension;
    public static ServerWorld dungeonPocketDimension;

    public static boolean isDimDoorsPocketDimension(World world) {
        RegistryKey<World> type = world.getRegistryKey();
        return type == PERSONAL || type == PUBLIC || type == DUNGEON;
    }

    public static boolean isLimboDimension(World world) {
        return world.getRegistryKey() == LIMBO || world.getDimension() == limboDimensionType || world == limboDimension;
    }

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ModDimensions.limboDimensionType = server.getRegistryManager().getDimensionTypes().get(LIMBO_TYPE);
            ModDimensions.pocketDimensionType = server.getRegistryManager().getDimensionTypes().get(POCKET_TYPE);
            ModDimensions.limboDimension = server.getWorld(LIMBO);
            ModDimensions.personalPocketDimension = server.getWorld(PERSONAL);
            ModDimensions.publicPocketDimension = server.getWorld(PUBLIC);
            ModDimensions.dungeonPocketDimension = server.getWorld(DUNGEON);
        });
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier("dimdoors", "blank"), BlankChunkGenerator.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier("dimdoors", "limbo_chunk_generator"), LimboChunkGenerator.CODEC);
        Registry.register(Registry.BIOME_SOURCE, new Identifier("dimdoors", "limbo_biome_source"), LimboBiomeSource.CODEC);
    }
}
