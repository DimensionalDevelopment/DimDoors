package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import org.dimdev.dimdoors.util.TeleportUtil;

import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.util.math.MathUtil;

public class DimTeleportCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dimteleport")
                .then(CommandManager
                        .argument("dimension", DimensionArgumentType.dimension())
                        .executes(ctx -> {
                            return teleport(ctx.getSource().getEntity(), DimensionArgumentType.getDimensionArgument(ctx, "dimension"), ctx.getSource().getPosition());
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

    private static int teleport(Entity entity, ServerWorld dimension, Vec3d pos) {
		TeleportUtil.teleport(entity, dimension, pos, MathUtil.entityEulerAngle(entity), entity.getVelocity());
        return Command.SINGLE_SUCCESS;
    }
}
