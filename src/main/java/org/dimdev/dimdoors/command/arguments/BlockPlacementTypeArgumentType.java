package org.dimdev.dimdoors.command.arguments;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import org.dimdev.dimdoors.api.util.BlockPlacementType;

public class BlockPlacementTypeArgumentType extends StringRepresentableArgument<BlockPlacementType> {
	public BlockPlacementTypeArgumentType() {
		super(BlockPlacementType.CODEC, BlockPlacementType::values);
	}

	public static StringRepresentableArgument<BlockPlacementType> blockPlacementType() {
		return new BlockPlacementTypeArgumentType();
	}

	public static BlockPlacementType getBlockPlacementType(CommandContext<CommandSourceStack> context, String id) {
		return context.getArgument(id, BlockPlacementType.class);
	}
}
