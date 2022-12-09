package org.dimdev.dimdoors.world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.BlankChunkGenerator;

import java.util.Objects;

public final class ModDimensions {
    public static final RegistryKey<World> LIMBO = RegistryKey.of(RegistryKeys.WORLD, DimensionalDoors.id("limbo"));
    public static final RegistryKey<World> PERSONAL = RegistryKey.of(RegistryKeys.WORLD, DimensionalDoors.id("personal_pockets"));
    public static final RegistryKey<World> PUBLIC = RegistryKey.of(RegistryKeys.WORLD, DimensionalDoors.id("public_pockets"));
    public static final RegistryKey<World> DUNGEON = RegistryKey.of(RegistryKeys.WORLD, DimensionalDoors.id("dungeon_pockets"));

    public static final RegistryKey<DimensionType> LIMBO_TYPE_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, DimensionalDoors.id("limbo"));
    public static final RegistryKey<DimensionType> POCKET_TYPE_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, DimensionalDoors.id("personal_pockets"));

    public static DimensionType LIMBO_TYPE;
    public static DimensionType POCKET_TYPE;

    public static ServerWorld LIMBO_DIMENSION;
    public static ServerWorld PERSONAL_POCKET_DIMENSION;
    public static ServerWorld PUBLIC_POCKET_DIMENSION;
    public static ServerWorld DUNGEON_POCKET_DIMENSION;

    public static boolean isPocketDimension(World world) {
        return isPocketDimension(world.getRegistryKey());
    }

    public static boolean isPrivatePocketDimension(World world) {
		return world != null && world == PERSONAL_POCKET_DIMENSION;
    }

    public static boolean isPocketDimension(RegistryKey<World> type) {
        return Objects.equals(type, PERSONAL) || Objects.equals(type, PUBLIC) || Objects.equals(type, DUNGEON);
    }

    public static boolean isLimboDimension(World world) {
        return world != null && world.getRegistryKey().equals(LIMBO);
    }

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ModDimensions.LIMBO_TYPE = server.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).get(LIMBO_TYPE_KEY);
            ModDimensions.POCKET_TYPE = server.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).get(POCKET_TYPE_KEY);
            ModDimensions.LIMBO_DIMENSION = server.getWorld(LIMBO);
            ModDimensions.PERSONAL_POCKET_DIMENSION = server.getWorld(PERSONAL);
            ModDimensions.PUBLIC_POCKET_DIMENSION = server.getWorld(PUBLIC);
            ModDimensions.DUNGEON_POCKET_DIMENSION = server.getWorld(DUNGEON);
        });
        Registry.register(Registries.CHUNK_GENERATOR, DimensionalDoors.id("blank"), BlankChunkGenerator.CODEC);
    }
}
