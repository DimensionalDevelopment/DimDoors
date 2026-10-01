package org.dimdev.dimdoors.command.arguments

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.StringRepresentableArgument
import org.dimdev.dimdoors.api.util.BlockPlacementType

object BlockPlacementTypeArgumentType : StringRepresentableArgument<BlockPlacementType>(BlockPlacementType.CODEC, BlockPlacementType::values) {


    fun getBlockPlacementType(context: CommandContext<CommandSourceStack>, id: String): BlockPlacementType? = context.getArgument(id, BlockPlacementType::class.java)
}
