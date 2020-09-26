package org.dimdev.dimdoors.command;

import com.mojang.brigadier.CommandDispatcher;
import org.dimdev.dimdoors.util.TeleportUtil;

import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class DimTeleportCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dimteleport")
                .then(CommandManager
                        .argument("dimension", DimensionArgumentType.dimension())
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            return teleport(player, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), player.getPos());
                        })
                        .then(CommandManager
                                .argument("coordinates", Vec3ArgumentType.vec3())
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    return teleport(player, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), Vec3ArgumentType.getVec3(ctx, "coordinates"));
                                })
                        )
                )
        );
    }

    private static int teleport(ServerPlayerEntity player, ServerWorld dimension, Vec3d pos) {
        player.moveToWorld(dimension);
        player.setPos(pos.x, pos.y, pos.z);
        return 1;
    }
}