package org.dimdev.dimdoors.world.pocket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.dimension.InfiniverseAPI;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;

public record WorldInfo(ResourceKey<Level> key, Holder<PocketGenerator> generatorId) {
    public static Codec<WorldInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Level.RESOURCE_KEY_CODEC.fieldOf("key").forGetter(WorldInfo::key),
            PocketGenerator.HOLDER_CODEC.fieldOf("generator").forGetter(WorldInfo::generatorId)
    ).apply(instance, WorldInfo::new));
    public ServerLevel getLevel() {
        var server = DimensionalDoors.getServer();

        return InfiniverseAPI.get().getOrCreateLevel(server, key, () -> getStem(server));
    }

    private LevelStem getStem(MinecraftServer server) {
        var typeHolder = server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(ModDimensions.POCKET_TYPE_KEY);
        var biomeHolder = server.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(ModBiomes.PUBLIC_BLACK_VOID_KEY);

        return new LevelStem(typeHolder, new PocketChunkGenerator(new FixedBiomeSource(biomeHolder), generatorId));
    }
}
