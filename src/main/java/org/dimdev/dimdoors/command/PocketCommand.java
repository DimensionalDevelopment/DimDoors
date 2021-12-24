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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PocketCommand {
	private static final Logger LOGGER = LogManager.getLogger();

	// TODO: probably move somewhere else
	public static final Map<UUID, ServerCommandSource> logSetting = new HashMap<>();

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
						.then(
								literal("log")
										// TODO: make command toggle logging of pocket creation to console if used from console
										.then(literal("creation")
												.requires(commandSource -> commandSource.getEntity() instanceof ServerPlayerEntity)
												.executes(ctx -> {
													ServerCommandSource commandSource = ctx.getSource();
													UUID playerUUID = commandSource.getPlayer().getUuid();
													if (logSetting.containsKey(playerUUID)) {
														logSetting.remove(playerUUID);
														commandSource.sendFeedback(new TranslatableText("commands.pocket.log.creation.off"), false);
													} else {
														logSetting.put(playerUUID, commandSource);
														commandSource.sendFeedback(new TranslatableText("commands.pocket.log.creation.on"), false);
													}
													return Command.SINGLE_SUCCESS;
												})
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
				source.getWorld(),
				source.getBlockPos(),
				blockPlacementType
		);

		String id = template.getId().toString();
		source.getCommandSource().sendFeedback(new TranslatableText("commands.pocket.placedSchem", id, "" + source.getBlockPos().getX() + ", " + source.getBlockPos().getY() + ", " + source.getBlockPos().getZ(), source.world.getRegistryKey().getValue().toString()), true);
		return Command.SINGLE_SUCCESS;
	}
}
