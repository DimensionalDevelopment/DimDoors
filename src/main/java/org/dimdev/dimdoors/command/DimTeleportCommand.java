package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.MathUtil;

import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

public class DimTeleportCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dimteleport")
                .then(CommandManager
                        .argument("dimension", DimensionArgumentType.dimension())
                        .executes(ctx -> {
							ServerPlayerEntity player = ctx.getSource().getPlayer();
                            return teleport(player, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), player.getPos(), MathUtil.entityEulerAngle(player));
                        })
                        .then(CommandManager
                                .argument("coordinates", Vec3ArgumentType.vec3())
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    return teleport(player, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), Vec3ArgumentType.getVec3(ctx, "coordinates"), MathUtil.entityEulerAngle(player));
                                })
								.then(CommandManager
										.argument("yaw", FloatArgumentType.floatArg())
										.executes( ctx -> {
											ServerPlayerEntity player = ctx.getSource().getPlayer();
											return teleport(player, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), Vec3ArgumentType.getVec3(ctx, "coordinates"), new EulerAngle(player.pitch, FloatArgumentType.getFloat(ctx, "yaw"), 0));
										})
										.then(CommandManager
												.argument("pitch", FloatArgumentType.floatArg())
												.executes( ctx -> {
													ServerPlayerEntity player = ctx.getSource().getPlayer();
													return teleport(player, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), Vec3ArgumentType.getVec3(ctx, "coordinates"), new EulerAngle(FloatArgumentType.getFloat(ctx, "pitch"), FloatArgumentType.getFloat(ctx, "yaw"), 0));
												})
										)
								)
                        )
                )
        );
    }

    private static int teleport(Entity entity, ServerWorld dimension, Vec3d pos, EulerAngle angle) {
		TeleportUtil.teleport(entity, dimension, pos, angle, entity.getVelocity());
        return Command.SINGLE_SUCCESS;
    }
}
