package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

public class DimTeleportCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dimteleport")
				.requires(source -> source.hasPermission(4))
                .then(Commands
                        .argument("dimension", DimensionArgument.dimension())
                        .executes(ctx -> {
							ServerPlayer player = ctx.getSource().getPlayer();
                            return teleport(player, DimensionArgument.getDimension(ctx, "dimension"), player.position(), MathUtil.entityEulerAngle(player));
                        })
                        .then(Commands
                                .argument("coordinates", Vec3Argument.vec3())
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayer();
                                    return teleport(player, DimensionArgument.getDimension(ctx, "dimension"), Vec3Argument.getVec3(ctx, "coordinates"), MathUtil.entityEulerAngle(player));
                                })
								.then(Commands
										.argument("yaw", FloatArgumentType.floatArg())
										.executes( ctx -> {
											ServerPlayer player = ctx.getSource().getPlayer();
											return teleport(player, DimensionArgument.getDimension(ctx, "dimension"), Vec3Argument.getVec3(ctx, "coordinates"), new Rotations(player.getXRot(), FloatArgumentType.getFloat(ctx, "yaw"), 0));
										})
										.then(Commands
												.argument("pitch", FloatArgumentType.floatArg())
												.executes( ctx -> {
													ServerPlayer player = ctx.getSource().getPlayer();
													return teleport(player, DimensionArgument.getDimension(ctx, "dimension"), Vec3Argument.getVec3(ctx, "coordinates"), new Rotations(FloatArgumentType.getFloat(ctx, "pitch"), FloatArgumentType.getFloat(ctx, "yaw"), 0));
												})
										)
								)
                        )
                )
        );
    }

    private static int teleport(Entity entity, ServerLevel dimension, Vec3 pos, Rotations angle) {
    	if(entity instanceof Player) {
			DimensionalRegistry.getRiftRegistry().setOverworldRift(entity.getUUID(), new Location((ServerLevel) entity.level(), entity.blockPosition()));
		}
		TeleportUtil.teleport(entity, dimension, pos, angle, entity.getDeltaMovement());
        return Command.SINGLE_SUCCESS;
    }
}
