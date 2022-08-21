package org.dimdev.dimdoors.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.command.arguments.ThemeArgumentType;
import org.dimdev.dimdoors.pockets.theme.Theme;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ThemeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("theme")
				.then(
						argument("theme", new ThemeArgumentType())
								.executes(ctx -> {
									Theme theme = ctx.getArgument("theme", Theme.class);
									ServerPlayerEntity entity = ctx.getSource().getPlayer();
									World world = entity.getWorld();
									BlockPos pos = entity.getBlockPos();

									BlockPos.streamOutwards(pos, 10, 10, 10).forEach(bp -> {
										world.setBlockState(bp, theme.apply(world.getBlockState(bp)));
									});

									return 1;
								})));
	}
}
