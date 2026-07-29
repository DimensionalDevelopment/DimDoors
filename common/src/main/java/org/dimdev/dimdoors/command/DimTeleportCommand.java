package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Rotations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.MathUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DimTeleportCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("dimteleport")
                        .requires(source -> source.hasPermission(2))
                        .then(create(ctx -> {
                            var player = ctx.getSource().getPlayer();

                            return player != null ? List.of(player) : Collections.emptyList();
                        }))
                        .then(
                                Commands.argument("entities", EntityArgument.entities()).then(create(ctx -> (Collection<Entity>) EntityArgument.getEntities(ctx, "entities")))
                        )
        );
    }

    private static Function<Entity, Vec3> POS_FROM_ENTITY = Entity::position;
    private static Function<Entity, Rotations> ANGLEFROM_ENTITY = MathUtil::entityEulerAngle;
    private static Function<Vec3, Function<Entity, Vec3>> CONSTANT_POS = pos -> entity -> pos;
    private static Function<Float, Function<Entity, Rotations>> CONSTANT_ROT_WITH_ENTITY_YAW = yaw -> entity -> new Rotations(entity.getXRot(), yaw, 0f);
    private static BiFunction<Float, Float, Function<Entity, Rotations>> CONSTANT_ROT = (pitch, yaw) -> {
        var rotation = new Rotations(pitch, yaw, 0f);
        return entity -> rotation;
    };

    private interface EntityExtractor {
        Collection<Entity> extract(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> create(EntityExtractor entityFunction) {
        return Commands
                .argument("dimension", DimensionArgument.dimension())
                .executes(ctx -> {
                    var list = entityFunction.extract(ctx);
                    return teleport(list, DimensionArgument.getDimension(ctx, "dimension"), POS_FROM_ENTITY, ANGLEFROM_ENTITY);
                })
                .then(Commands
                        .argument("coordinates", Vec3Argument.vec3())
                        .executes(ctx -> {
                            var list = entityFunction.extract(ctx);
                            return teleport(list, DimensionArgument.getDimension(ctx, "dimension"), CONSTANT_POS.apply(Vec3Argument.getVec3(ctx, "coordinates")), ANGLEFROM_ENTITY);
                        })
                        .then(Commands
                                .argument("yaw", FloatArgumentType.floatArg())
                                .executes( ctx -> {
                                    var list = entityFunction.extract(ctx);
                                    return teleport(list, DimensionArgument.getDimension(ctx, "dimension"), CONSTANT_POS.apply(Vec3Argument.getVec3(ctx, "coordinates")), CONSTANT_ROT_WITH_ENTITY_YAW.apply(FloatArgumentType.getFloat(ctx, "yaw")));
                                })
                                .then(Commands
                                        .argument("pitch", FloatArgumentType.floatArg())
                                        .executes( ctx -> {
                                            var list = entityFunction.extract(ctx);
                                            return teleport(list, DimensionArgument.getDimension(ctx, "dimension"), CONSTANT_POS.apply(Vec3Argument.getVec3(ctx, "coordinates")), CONSTANT_ROT.apply(FloatArgumentType.getFloat(ctx, "pitch"), FloatArgumentType.getFloat(ctx, "yaw")));
                                        })
                                )
                        )
                );
    }

    private static int teleport(Collection<Entity> list, ServerLevel dimension, Function<Entity, Vec3> pos, Function<Entity, Rotations> angle) {
        for (var entity : list) {
            TeleportUtil.teleport(entity, dimension, pos.apply(entity), angle.apply(entity), entity.getDeltaMovement());
        }

//    TeleportUtil.teleport(entity, dimension, pos, angle, entity.getDeltaMovement());
        return Command.SINGLE_SUCCESS;
    }
}
