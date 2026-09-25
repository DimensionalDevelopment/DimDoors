package org.dimdev.dimdoors.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.DimensionArgument
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.core.Rotations
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.cast
import org.dimdev.dimdoors.api.util.TeleportUtil
import org.dimdev.dimdoors.api.util.math.MathUtil.entityEulerAngle

object DimTeleportCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register("dimteleport") {
            requires { source -> source.hasPermission(2) }
            create { getSource().player?.let { mutableListOf(it) } ?: mutableListOf() }
            argument("entities", EntityArgument.entities()) {
                create { EntityArgument.getEntities(this, "entities").cast() }
            }
        }
    }

    private val POS_FROM_ENTITY: (Entity) -> Vec3 = { obj: Entity -> obj.position() }
    private val ANGLEFROM_ENTITY: (Entity) -> Rotations = ::entityEulerAngle
    private val CONSTANT_POS: (Vec3) -> (Entity) -> Vec3 = { pos -> { pos } }
    private val CONSTANT_ROT_WITH_ENTITY_YAW: (Float) -> (Entity) -> Rotations = { yaw -> { entity -> Rotations(entity.xRot, yaw, 0f) } }
    private val CONSTANT_ROT: (Float, Float) -> (Entity) -> Rotations = { pitch, yaw -> { Rotations(pitch, yaw, 0f) } }

    private fun ArgumentBuilder<CommandSourceStack, *>.create(entityFunction: CommandContext<CommandSourceStack>.() -> MutableCollection<Entity>): ArgumentBuilder<CommandSourceStack, *> {
        fun CommandContext<CommandSourceStack>.list() = this.let(entityFunction)

        return argument("dimension", DimensionArgument.dimension()) {
            execute { teleport(list(), dimension, POS_FROM_ENTITY, ANGLEFROM_ENTITY) }
            argument("coordinates", Vec3Argument.vec3()) {
                execute { teleport(list(), dimension, coordinates.let(CONSTANT_POS), ANGLEFROM_ENTITY) }
                argument("yaw", FloatArgumentType.floatArg()) {
                    execute { teleport(list(), dimension, coordinates.let(CONSTANT_POS), yaw.let(CONSTANT_ROT_WITH_ENTITY_YAW)) }
                    argument("pitch", FloatArgumentType.floatArg()) {
                        execute { teleport(list(), dimension, coordinates.let(CONSTANT_POS), CONSTANT_ROT.invoke(pitch, yaw)) }
                    }
                }
            }
        }
    }

    private fun teleport(
        list: MutableCollection<Entity>,
        dimension: ServerLevel,
        pos: (Entity) -> Vec3,
        angle: (Entity) -> Rotations
    ): Int {
        for (entity in list) {
            TeleportUtil.teleport(entity, dimension, pos.invoke(entity), angle.invoke(entity), entity.deltaMovement)
        }

        return Command.SINGLE_SUCCESS
    }
}
