package org.dimdev.dimdoors.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import org.dimdev.dimdoors.world.DataValues
import java.util.function.Supplier
import java.util.function.UnaryOperator

object FrayCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register("fray") {
            requires { source -> source.hasPermission(2) }
            executes { context -> get(context, target(context)) }
            literal("get") {
                executes { context -> get(context, target(context)) }
                argument("target", EntityArgument.entity()) {
                    executes { context -> get(context, EntityArgument.getEntity(context, "target")) }
                }
            }

            literal("get_or_create") {
                executes { context -> getOrCreate(context, target(context)) }
                argument("target", EntityArgument.entity()) {
                    executes { context -> getOrCreate(context, EntityArgument.getEntity(context, "target")) }
                }
            }

            literal("has") {
                executes { context -> has(context, target(context)) }
                argument("target", EntityArgument.entity()) {
                    executes { context -> has(context, EntityArgument.getEntity(context, "target")) }
                }
            }

            literal("set") {
                argument("value", IntegerArgumentType.integer()) {
                    executes { context -> set(context, target(context)) }
                    argument("target", EntityArgument.entity()) {
                        executes { context -> set(context, EntityArgument.getEntity(context, "target")) }
                    }
                }
            }

            literal("add") {
                argument("amount", IntegerArgumentType.integer()) {
                    executes { context -> add(context, target(context)) }
                    argument("target", EntityArgument.entity()) {
                        executes { context -> add(context, EntityArgument.getEntity(context, "target")) }
                    }
                }
            }

            literal("remove") {
                executes { context -> remove(context, target(context)) }
                argument("target", EntityArgument.entity()) {
                    executes { context -> remove(context, EntityArgument.getEntity(context, "target")) }
                }
            }
        }
    }

    private fun get(context: CommandContext<CommandSourceStack>, target: Entity): Int {
        val value = DataValues.FRAY_VALUE.get(target)
        context.getSource()!!.sendSuccess(
            Supplier { Component.literal("Fray value for " + name(target) + ": " + (if (value == null) "<unset>" else value)) },
            false
        )
        return Command.SINGLE_SUCCESS
    }

    private fun getOrCreate(context: CommandContext<CommandSourceStack?>, target: Entity): Int {
        val value = DataValues.FRAY_VALUE.getOrCreate(target)
        context.getSource()!!
            .sendSuccess(Supplier { Component.literal("Fray value for " + name(target) + ": " + value) }, false)
        return Command.SINGLE_SUCCESS
    }

    private fun has(context: CommandContext<CommandSourceStack?>, target: Entity): Int {
        val hasValue = DataValues.FRAY_VALUE.has(target)
        context.getSource()!!.sendSuccess(
            Supplier { Component.literal("Fray value for " + name(target) + " is " + (if (hasValue) "set" else "unset")) },
            false
        )
        return if (hasValue) 1 else 0
    }

    private fun set(context: CommandContext<CommandSourceStack?>, target: Entity): Int {
        val value = IntegerArgumentType.getInteger(context, "value")
        DataValues.FRAY_VALUE.set(target, value)
        context.getSource()!!
            .sendSuccess(Supplier { Component.literal("Fray value for " + name(target) + " set to " + value) }, false)
        return Command.SINGLE_SUCCESS
    }

    private fun add(context: CommandContext<CommandSourceStack?>, target: Entity): Int {
        val amount = IntegerArgumentType.getInteger(context, "amount")
        DataValues.FRAY_VALUE.update(target, 0, UnaryOperator { value: Int? -> value!! + amount })
        val value = DataValues.FRAY_VALUE.getOrCreate(target)
        context.getSource()!!
            .sendSuccess(Supplier { Component.literal("Fray value for " + name(target) + " set to " + value) }, false)
        return Command.SINGLE_SUCCESS
    }

    private fun remove(context: CommandContext<CommandSourceStack?>, target: Entity): Int {
        DataValues.FRAY_VALUE.remove(target)
        context.getSource()!!
            .sendSuccess(Supplier { Component.literal("Fray value for " + name(target) + " removed") }, false)
        return Command.SINGLE_SUCCESS
    }

    @Throws(CommandSyntaxException::class)
    private fun target(context: CommandContext<CommandSourceStack>): Entity {
        return context.getSource()!!.entityOrException
    }

    private fun name(target: Entity): String {
        return target.getDisplayName()!!.getString()
    }
}
