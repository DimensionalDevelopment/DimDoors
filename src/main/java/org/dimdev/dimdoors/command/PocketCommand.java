package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.command.arguments.EnumArgumentType;
import org.dimdev.dimdoors.command.arguments.PocketTemplateArgumentType;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import net.fabricmc.loader.api.FabricLoader;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PocketCommand {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
				literal("pocket")
						.requires(source -> source.hasPermissionLevel(2))
						.then(
								literal("schematic")
										.then(
												literal("place")
														.then(
																argument("pocket_template", new PocketTemplateArgumentType())
																		.executes(ctx -> place(ctx.getSource().getPlayer(), PocketTemplateArgumentType.getValue(ctx, "pocket_template"), BlockPlacementType.SECTION_NO_UPDATE))
																		.then(
																				argument("placement_type", new EnumArgumentType<>(BlockPlacementType.class))
																						.executes(ctx -> place(ctx.getSource().getPlayer(), PocketTemplateArgumentType.getValue(ctx, "pocket_template"), ctx.getArgument("placement_type", BlockPlacementType.class)))
																		)
														)
										)
										.then(
												literal("load")
														.requires(source -> FabricLoader.getInstance().isModLoaded("worldedit"))
														.then(
																argument("pocket_template", new PocketTemplateArgumentType())
																		.executes(ctx -> load(ctx.getSource(), PocketTemplateArgumentType.getValue(ctx, "pocket_template")))
														)
										)
						)
		);
	}

	private static int load(ServerCommandSource source, PocketTemplate template) throws CommandSyntaxException {
		try {
			return WorldeditHelper.load(source, template);
		} catch (NoClassDefFoundError e) {
			return 0;
		}
	}

	private static int place(ServerPlayerEntity source, PocketTemplate template, BlockPlacementType blockPlacementType) throws CommandSyntaxException {
		SchematicPlacer.place(
				template.getSchematic(),
				source.getServerWorld(),
				source.getBlockPos(),
				blockPlacementType
		);

		String id = template.getId().toString();
		source.getCommandSource().sendFeedback(new TranslatableText("commands.pocket.placedSchem", id, "" + source.getBlockPos().getX() + ", " + source.getBlockPos().getY() + ", " + source.getBlockPos().getZ(), source.world.getRegistryKey().getValue().toString()), true);
		return Command.SINGLE_SUCCESS;
	}
}
