package org.dimdev.dimdoors.command.arguments;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import org.dimdev.dimdoors.api.util.BlockPlacementType;

public class BlockPlacementTypeArgumentType extends EnumArgumentType<BlockPlacementType> {
	public BlockPlacementTypeArgumentType() {
		super(BlockPlacementType.CODEC, BlockPlacementType::values);
	}

	public static EnumArgumentType<BlockPlacementType> blockPlacementType() {
		return new BlockPlacementTypeArgumentType();
	}

	public static BlockPlacementType getBlockPlacementType(CommandContext<ServerCommandSource> context, String id) {
		return context.getArgument(id, BlockPlacementType.class);
	}
}
