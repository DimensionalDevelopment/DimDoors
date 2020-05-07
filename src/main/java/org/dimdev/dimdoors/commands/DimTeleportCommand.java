package org.dimdev.dimdoors.commands;


import com.google.gson.Gson;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.command.arguments.*;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.dimdev.util.TeleportUtil;

public class DimTeleportCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dimteleport")
                .then(CommandManager
                        .argument("dimension", DimensionArgumentType.dimension())
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            return teleport(player, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), player.getPos(), player.getYaw(1.0f));
                        }))
                .then(CommandManager
                        .argument("coordinates", Vec3ArgumentType.vec3())
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            return teleport(player, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), Vec3ArgumentType.getVec3(ctx, "coordinates"), player.getYaw(1.0f));
                        }))
                .then(CommandManager
                        .argument("yaw", FloatArgumentType.floatArg())
                        .executes(ctx -> teleport(ctx.getSource().getPlayer(), DimensionArgumentType.getDimensionArgument(ctx, "dimension"), Vec3ArgumentType.getVec3(ctx, "coordinates"), FloatArgumentType.getFloat(ctx, "yaw")))
                )
        );
    }

    private static int teleport(ServerPlayerEntity player, DimensionType dimension, Vec3d coordinates, float yaw) {
        try {
            TeleportUtil.teleport(player, dimension, coordinates, yaw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }
}
