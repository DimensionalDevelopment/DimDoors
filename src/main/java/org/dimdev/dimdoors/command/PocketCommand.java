package org.dimdev.dimdoors.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.command.arguments.BlockPlacementTypeArgumentType;
import org.dimdev.dimdoors.command.arguments.PocketTemplateArgumentType;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PocketCommand {
	private static final Logger LOGGER = LogManager.getLogger();

	// TODO: probably move somewhere else
	public static final Map<UUID, CommandSourceStack> logSetting = new HashMap<>();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				literal("pocket")
						.requires(source -> source.hasPermission(2))
						.then(
								literal("schematic")
										.then(
												literal("place")
														.then(
																argument("pocket_template", new PocketTemplateArgumentType())
																		.executes(ctx -> place(ctx.getSource().getPlayer(), PocketTemplateArgumentType.getValue(ctx, "pocket_template"), BlockPlacementType.SECTION_NO_UPDATE))
																		.then(
																				argument("placement_type", new BlockPlacementTypeArgumentType())
																						.executes(ctx -> place(ctx.getSource().getPlayer(), PocketTemplateArgumentType.getValue(ctx, "pocket_template"), BlockPlacementTypeArgumentType.getBlockPlacementType(ctx, "placement_type")))
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
												.requires(commandSource -> commandSource.getEntity() instanceof ServerPlayer)
												.executes(ctx -> {
													CommandSourceStack commandSource = ctx.getSource();
													UUID playerUUID = commandSource.getPlayer().getUUID();
													if (logSetting.containsKey(playerUUID)) {
														logSetting.remove(playerUUID);
														commandSource.sendSuccess(MutableComponent.create(new TranslatableContents("commands.pocket.log.creation.off")), false);
													} else {
														logSetting.put(playerUUID, commandSource);
														commandSource.sendSuccess(MutableComponent.create(new TranslatableContents("commands.pocket.log.creation.on")), false);
													}
													return Command.SINGLE_SUCCESS;
												})
										)

						)
		);
	}

	private static int load(CommandSourceStack source, PocketTemplate template) throws CommandSyntaxException {
		try {
			return WorldeditHelper.load(source, template);
		} catch (NoClassDefFoundError e) {
			return 0;
		}
	}

	private static int place(ServerPlayer source, PocketTemplate template, BlockPlacementType blockPlacementType) throws CommandSyntaxException {
		SchematicPlacer.place(
				template.getSchematic(),
				source.getLevel(),
				source.blockPosition(),
				blockPlacementType
		);

		String id = template.getId().toString();
		source.createCommandSourceStack().sendSuccess(MutableComponent.create(new TranslatableContents("commands.pocket.placedSchem", id, "" + source.blockPosition().getX() + ", " + source.blockPosition().getY() + ", " + source.blockPosition().getZ(), source.level.dimension().location().toString())), true);
		return Command.SINGLE_SUCCESS;
	}
}
