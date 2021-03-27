package org.dimdev.dimdoors.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.command.arguments.PocketTemplateArgumentType;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SchematicCommand {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("schematic") // TODO: better command name
				.then(argument("pocket_template", new PocketTemplateArgumentType())
						.executes(SchematicCommand::place)
				)
		);
	}

	private static int place(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity player = ctx.getSource().getPlayer();
		PocketTemplate template = PocketTemplateArgumentType.getValue(ctx, "pocket_template");

		SchematicPlacer.place(
				template.getSchematic(),
				ctx.getSource().getWorld(),
				ctx.getSource().getPlayer().getBlockPos(),
				BlockPlacementType.SECTION_NO_UPDATE // TODO: placement type argument
		);

		// TODO
		//LOGGER.info(String.format("Placed schematic %s from namespace %s at %s in world %s", id, ns, player.getBlockPos(), player.getServerWorld().getRegistryKey().getValue()));
		return 1;
	}
}
