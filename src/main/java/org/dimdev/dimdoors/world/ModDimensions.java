package org.dimdev.dimdoors.world;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

import com.google.common.collect.ImmutableMap;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.mixin.ChunkGeneratorSettingsAccessor;
import org.dimdev.dimdoors.mixin.DimensionTypeAccessor;
import org.dimdev.dimdoors.world.limbo.LimboChunkGenerator;
import org.dimdev.dimdoors.world.pocket.BlankChunkGenerator;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class ModDimensions {
    public static final RegistryKey<World> LIMBO = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:limbo"));
    public static final RegistryKey<World> PERSONAL = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:personal_pockets"));
    public static final RegistryKey<World> PUBLIC = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:public_pockets"));
    public static final RegistryKey<World> DUNGEON = RegistryKey.of(Registry.DIMENSION, new Identifier("dimdoors:dungeon_pockets"));

    public static final RegistryKey<DimensionType> LIMBO_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("dimdoors:limbo"));
    public static final RegistryKey<DimensionType> POCKET_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("dimdoors:personal_pockets"));

    public static DimensionType LIMBO_TYPE = DimensionTypeAccessor.invokeInit(OptionalLong.of(6000), false, false, false, false, 4.0, false, false, true, false, 256, BlockTags.INFINIBURN_OVERWORLD.getId(), DimensionType.THE_END_ID, 0.1F);
    public static DimensionType POCKET_TYPE = DimensionTypeAccessor.invokeInit(OptionalLong.empty(), true, false, false, false, 4.0, false, false, true, false, 256, BlockTags.INFINIBURN_OVERWORLD.getId(), DimensionType.THE_END_ID, 0.1F);

    public static final ChunkGeneratorSettings LIMBO_CHUNK_GENERATOR_SETTINGS;

    public static ServerWorld LIMBO_DIMENSION;
    public static ServerWorld PERSONAL_POCKET_DIMENSION;
    public static ServerWorld PUBLIC_POCKET_DIMENSION;
    public static ServerWorld DUNGEON_POCKET_DIMENSION;

    public static boolean isPocketDimension(World world) {
        return isPocketDimension(world.getRegistryKey());
    }

    public static boolean isPocketDimension(RegistryKey<World> type) {
        return Objects.equals(type, PERSONAL) || Objects.equals(type, PUBLIC) || Objects.equals(type, DUNGEON);
    }

    public static boolean isLimbo(StructureWorldAccess world) {
        return world != null && (world.getDimension() == LIMBO_TYPE || world == LIMBO_DIMENSION);
    }

    public static boolean isLimboDimension(World world) {
        return world != null && (world.getRegistryKey().equals(LIMBO) || world.getDimension() == LIMBO_TYPE || world == LIMBO_DIMENSION);
    }

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ModDimensions.LIMBO_TYPE = server.getRegistryManager().getDimensionTypes().get(LIMBO_TYPE_KEY);
            ModDimensions.POCKET_TYPE = server.getRegistryManager().getDimensionTypes().get(POCKET_TYPE_KEY);
            ModDimensions.LIMBO_DIMENSION = server.getWorld(LIMBO);
            ModDimensions.PERSONAL_POCKET_DIMENSION = server.getWorld(PERSONAL);
            ModDimensions.PUBLIC_POCKET_DIMENSION = server.getWorld(PUBLIC);
            ModDimensions.DUNGEON_POCKET_DIMENSION = server.getWorld(DUNGEON);
        });
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier("dimdoors", "blank"), BlankChunkGenerator.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier("dimdoors", "limbo_chunk_generator"), LimboChunkGenerator.CODEC);
    }

    static {
        StructuresConfig limboStructuresConfig = new StructuresConfig(
                Optional.of(StructuresConfig.DEFAULT_STRONGHOLD),
                ImmutableMap.of()
        );
        GenerationShapeConfig limboShapeConfig = new GenerationShapeConfig(
                178,
                new NoiseSamplingConfig(
                        1.000009876543,
                        2.9999876545678,
                        60,
                        240
                ),
                new SlideConfig(
                        -10,
                        3,
                        0
                ),
                new SlideConfig(
                        -30,
                        0,
                        0
                ),
                1,
                4,
                1,
                -0.26875,
                false,
                true,
                false,
                false
        );
        LIMBO_CHUNK_GENERATOR_SETTINGS = ChunkGeneratorSettingsAccessor.invokeInit(
                limboStructuresConfig,
                limboShapeConfig,
                ModBlocks.UNRAVELLED_FABRIC.getDefaultState(),
                ModBlocks.BLACK_ANCIENT_FABRIC.getDefaultState(),
                -10,
                5,
                8,
                false
        );
    }
}
