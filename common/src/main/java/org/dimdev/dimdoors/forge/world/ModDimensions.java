package org.dimdev.dimdoors.forge.world;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.registries.DeferredRegister;
<<<<<<< HEAD:common/src/main/java/org/dimdev/dimdoors/forge/world/ModDimensions.java
import net.minecraft.core.Registry;
=======
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
>>>>>>> merge-branch:common/src/main/java/org/dimdev/dimdoors/world/ModDimensions.java
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.forge.world.pocket.BlankChunkGenerator;

import java.util.Objects;
import java.util.OptionalLong;

public final class ModDimensions {
    public static final ResourceKey<Level> LIMBO = ResourceKey.create(Registry.DIMENSION_REGISTRY, DimensionalDoors.id("limbo"));
    public static final ResourceKey<Level> PERSONAL = ResourceKey.create(Registry.DIMENSION_REGISTRY, DimensionalDoors.id("personal_pockets"));
    public static final ResourceKey<Level> PUBLIC = ResourceKey.create(Registry.DIMENSION_REGISTRY, DimensionalDoors.id("public_pockets"));
    public static final ResourceKey<Level> DUNGEON = ResourceKey.create(Registry.DIMENSION_REGISTRY, DimensionalDoors.id("dungeon_pockets"));

<<<<<<< HEAD:common/src/main/java/org/dimdev/dimdoors/forge/world/ModDimensions.java
    public static final ResourceKey<DimensionType> LIMBO_TYPE_KEY = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, DimensionalDoors.id("limbo"));
    public static final ResourceKey<DimensionType> POCKET_TYPE_KEY = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, DimensionalDoors.id("personal_pockets"));
=======
    public static final ResourceKey<LevelStem> LIMBO_STEM = ResourceKey.create(Registries.LEVEL_STEM, DimensionalDoors.id("limbo"));
    public static final ResourceKey<LevelStem> PERSONAL_STEM = ResourceKey.create(Registries.LEVEL_STEM, DimensionalDoors.id("person"));
    public static final ResourceKey<LevelStem> PUBLIC_STEM = ResourceKey.create(Registries.LEVEL_STEM, DimensionalDoors.id("public"));
    public static final ResourceKey<LevelStem> DUNGEON_STEM = ResourceKey.create(Registries.LEVEL_STEM, DimensionalDoors.id("dungeon"));

    public static final ResourceKey<DimensionType> LIMBO_TYPE_KEY = ResourceKey.create(Registries.DIMENSION_TYPE, DimensionalDoors.id("limbo"));
    public static final ResourceKey<DimensionType> POCKET_TYPE_KEY = ResourceKey.create(Registries.DIMENSION_TYPE, DimensionalDoors.id("pocket"));
>>>>>>> merge-branch:common/src/main/java/org/dimdev/dimdoors/world/ModDimensions.java

    public static DimensionType LIMBO_TYPE;
    public static DimensionType POCKET_TYPE;

    public static ServerLevel LIMBO_DIMENSION;
    public static ServerLevel PERSONAL_POCKET_DIMENSION;
    public static ServerLevel PUBLIC_POCKET_DIMENSION;
    public static ServerLevel DUNGEON_POCKET_DIMENSION;

    public static boolean isPocketDimension(Level world) {
        return isPocketDimension(world.dimension());
    }

    public static boolean isPrivatePocketDimension(Level world) {
		return world != null && world == PERSONAL_POCKET_DIMENSION;
    }

    public static boolean isPocketDimension(ResourceKey<Level> type) {
        return Objects.equals(type, PERSONAL) || Objects.equals(type, PUBLIC) || Objects.equals(type, DUNGEON);
    }


    public static boolean isDungeonDimension(ResourceKey<Level> type) {
        return Objects.equals(type, PERSONAL) || Objects.equals(type, PUBLIC) || Objects.equals(type, DUNGEON);
    }

    public static boolean isLimboDimension(Level world) {
        return world != null && world.dimension().equals(LIMBO);
    }

    public static void init() {
        LifecycleEvent.SERVER_STARTED.register(server -> {
            ModDimensions.LIMBO_TYPE = server.registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(LIMBO_TYPE_KEY);
            ModDimensions.POCKET_TYPE = server.registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(POCKET_TYPE_KEY);
            ModDimensions.LIMBO_DIMENSION = server.getLevel(LIMBO);
            ModDimensions.PERSONAL_POCKET_DIMENSION = server.getLevel(PERSONAL);
            ModDimensions.PUBLIC_POCKET_DIMENSION = server.getLevel(PUBLIC);
            ModDimensions.DUNGEON_POCKET_DIMENSION = server.getLevel(DUNGEON);
        });
<<<<<<< HEAD:common/src/main/java/org/dimdev/dimdoors/forge/world/ModDimensions.java
        var deffered =DeferredRegister.create(DimensionalDoors.MOD_ID, Registry.CHUNK_GENERATOR_REGISTRY);
=======
        var deffered = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.CHUNK_GENERATOR);
>>>>>>> merge-branch:common/src/main/java/org/dimdev/dimdoors/world/ModDimensions.java
        deffered.register("blank", () -> BlankChunkGenerator.CODEC);
        deffered.register();
    }

    public static void bootstrap(BootstapContext<DimensionType> entries) {
        entries.register(LIMBO_TYPE_KEY, new DimensionType(OptionalLong.of(6000), true, false, false, false, 4, false, true, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, DimensionalDoors.id("limbo"), 0.1f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)));
        entries.register(POCKET_TYPE_KEY, new DimensionType(OptionalLong.of(6000), true, false, false, false, 4, false, true, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD, DimensionalDoors.id("dungeon"), 0.1f, new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)));
    }

    private static LevelStem createPocketStem(Holder<DimensionType> dimensionType, Holder<Biome> biome) {
        return new LevelStem(dimensionType, BlankChunkGenerator.of(new FixedBiomeSource(biome)));
    }
}
