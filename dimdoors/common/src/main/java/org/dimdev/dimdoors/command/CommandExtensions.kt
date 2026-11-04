package org.dimdev.dimdoors.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.DimensionArgument
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.Type
import org.dimdev.dimcore.command.TypeCommands

fun ArgumentBuilder<CommandSourceStack, *>.literal(name: String, block: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit): ArgumentBuilder<CommandSourceStack, *> = this.then(Commands.literal(name).also(block))
fun <V> ArgumentBuilder<CommandSourceStack, *>.argument(name: String, type: ArgumentType<V>, block: RequiredArgumentBuilder<CommandSourceStack, V>.() -> Unit): ArgumentBuilder<CommandSourceStack, *> = this.then(Commands.argument<V>(name, type).also(block))
fun ArgumentBuilder<CommandSourceStack, *>.entity(name: String, block: RequiredArgumentBuilder<CommandSourceStack, EntitySelector>.() -> Unit) = argument(name, EntityArgument.entities(), block)
fun ArgumentBuilder<CommandSourceStack, *>.integer(name: String, block: RequiredArgumentBuilder<CommandSourceStack, Int>.() -> Unit) = argument(name, IntegerArgumentType.integer(), block)
fun ArgumentBuilder<CommandSourceStack, *>.dimension(name: String, block: RequiredArgumentBuilder<CommandSourceStack, ResourceLocation>.() -> Unit) = argument(name, DimensionArgument.dimension(), block)
fun CommandDispatcher<CommandSourceStack>.register(name: String, block: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit): LiteralCommandNode<CommandSourceStack> = this.register(Commands.literal(name).also(block))
fun <B : Any> ArgumentBuilder<CommandSourceStack, *>.branch(name: String, registry: Registry<out Type<B>>, current: (CommandContext<CommandSourceStack>) -> B? = { null }, apply: (CommandContext<CommandSourceStack>, B) -> Int): ArgumentBuilder<CommandSourceStack, *> = then(TypeCommands.branch(name, registry, current, apply))
fun <T> argument(name: String, type: ArgumentType<T>, block: RequiredArgumentBuilder<CommandSourceStack, T>.() -> Unit): RequiredArgumentBuilder<CommandSourceStack, T> = Commands.argument(name, type).also(block)
fun ArgumentBuilder<CommandSourceStack, *>.execute(block: CommandContext<CommandSourceStack>.() -> Int): ArgumentBuilder<CommandSourceStack, *> = executes { ctx -> ctx.let(block) }
val CommandContext<CommandSourceStack>.dimension: ServerLevel get() = DimensionArgument.getDimension(this, "dimension")
val CommandContext<CommandSourceStack>.coordinates: Vec3 get() = Vec3Argument.getVec3(this, "coordinates")
val CommandContext<CommandSourceStack>.yaw get() = FloatArgumentType.getFloat(this, "yaw")
val CommandContext<CommandSourceStack>.pitch get() = FloatArgumentType.getFloat(this, "pitch")
