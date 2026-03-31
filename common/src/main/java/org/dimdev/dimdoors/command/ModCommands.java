package org.dimdev.dimdoors.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.dimension.InfiniverseAPI;

import static net.irisshaders.iris.Iris.MODID;

public final class ModCommands {
    public static void init() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, dedicated) -> {
            DimTeleportCommand.register(dispatcher);
            PocketCommand.register(dispatcher);
            StandingInAir.register(dispatcher);
            onRegisterCommands(dispatcher);
        });
    }

    public static void onRegisterCommands(CommandDispatcher<CommandSourceStack> event) {
        event.register(Commands.literal("infinive-rse_examplemod")
                .then(Commands.literal("create_dimension")
                        .executes(ModCommands::createDimension))
                .then(Commands.literal("remove_dimension")
                        .executes(ModCommands::removeDimension)));
    }

    public static final ResourceKey<Level> LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, DimensionalDoors.id("example_dimension"));

    public static int createDimension(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        try
        {
            InfiniverseAPI.get().getOrCreateLevel(context.getSource().getServer(), LEVEL_KEY, () -> createLevel(context.getSource().getServer()));
        } catch (Exception e)
        {
            throw new SimpleCommandExceptionType(Component.literal(e.getMessage())).create();
        }

        return 1;
    }

    public static int removeDimension(CommandContext<CommandSourceStack> context)
    {
        InfiniverseAPI.get().markDimensionForUnregistration(context.getSource().getServer(), LEVEL_KEY);

        return 1;
    }

    public static LevelStem createLevel(MinecraftServer server)
    {
        ServerLevel oldLevel = server.overworld();
        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, server.registryAccess());
        ChunkGenerator oldChunkGenerator = oldLevel.getChunkSource().getGenerator();
        ChunkGenerator newChunkGenerator = ChunkGenerator.CODEC.encodeStart(ops, oldChunkGenerator)
                .flatMap(nbt -> ChunkGenerator.CODEC.parse(ops, nbt))
                .getOrThrow(s -> new RuntimeException(String.format("Error copying dimension: {}", s)));
        Holder<DimensionType> typeHolder = oldLevel.dimensionTypeRegistration();
        return new LevelStem(typeHolder, newChunkGenerator);
    }
}
