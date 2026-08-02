package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.dimdev.dimdoors.world.fray.ModDataValues;

public final class FrayCommand {
    private FrayCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fray")
                .requires(source -> source.hasPermission(2))
                .executes(context -> get(context, target(context)))
                .then(Commands.literal("get")
                        .executes(context -> get(context, target(context)))
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(context -> get(context, EntityArgument.getEntity(context, "target")))))
                .then(Commands.literal("get_or_create")
                        .executes(context -> getOrCreate(context, target(context)))
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(context -> getOrCreate(context, EntityArgument.getEntity(context, "target")))))
                .then(Commands.literal("has")
                        .executes(context -> has(context, target(context)))
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(context -> has(context, EntityArgument.getEntity(context, "target")))))
                .then(Commands.literal("set")
                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(context -> set(context, target(context)))
                                .then(Commands.argument("target", EntityArgument.entity())
                                        .executes(context -> set(context, EntityArgument.getEntity(context, "target"))))))
                .then(Commands.literal("add")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(context -> add(context, target(context)))
                                .then(Commands.argument("target", EntityArgument.entity())
                                        .executes(context -> add(context, EntityArgument.getEntity(context, "target"))))))
                .then(Commands.literal("remove")
                        .executes(context -> remove(context, target(context)))
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(context -> remove(context, EntityArgument.getEntity(context, "target"))))));
    }

    private static int get(CommandContext<CommandSourceStack> context, Entity target) {
        Integer value = ModDataValues.FRAY_VALUE.get(target);
        context.getSource().sendSuccess(() -> Component.literal("Fray value for " + name(target) + ": " + (value == null ? "<unset>" : value)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int getOrCreate(CommandContext<CommandSourceStack> context, Entity target) {
        int value = ModDataValues.FRAY_VALUE.getOrCreate(target);
        context.getSource().sendSuccess(() -> Component.literal("Fray value for " + name(target) + ": " + value), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int has(CommandContext<CommandSourceStack> context, Entity target) {
        boolean hasValue = ModDataValues.FRAY_VALUE.has(target);
        context.getSource().sendSuccess(() -> Component.literal("Fray value for " + name(target) + " is " + (hasValue ? "set" : "unset")), false);
        return hasValue ? 1 : 0;
    }

    private static int set(CommandContext<CommandSourceStack> context, Entity target) {
        int value = IntegerArgumentType.getInteger(context, "value");
        ModDataValues.FRAY_VALUE.set(target, value);
        context.getSource().sendSuccess(() -> Component.literal("Fray value for " + name(target) + " set to " + value), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int add(CommandContext<CommandSourceStack> context, Entity target) {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ModDataValues.FRAY_VALUE.update(target, 0, value -> value + amount);
        int value = ModDataValues.FRAY_VALUE.getOrCreate(target);
        context.getSource().sendSuccess(() -> Component.literal("Fray value for " + name(target) + " set to " + value), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int remove(CommandContext<CommandSourceStack> context, Entity target) {
        ModDataValues.FRAY_VALUE.remove(target);
        context.getSource().sendSuccess(() -> Component.literal("Fray value for " + name(target) + " removed"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static Entity target(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return context.getSource().getEntityOrException();
    }

    private static String name(Entity target) {
        return target.getDisplayName().getString();
    }
}
