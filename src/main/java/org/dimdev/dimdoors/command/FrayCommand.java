package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.world.level.component.PlayerModifiersComponent;

public class FrayCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("fray")
				.then(CommandManager
						.argument("amount", IntegerArgumentType.integer())
						.executes(ctx -> {
							ServerPlayerEntity player = ctx.getSource().getPlayer();

							return addFray(player, IntegerArgumentType.getInteger(ctx, "amount"));
						})
				)
		);
	}
	private static int addFray(PlayerEntity player, int amount) {
		PlayerModifiersComponent.incrementFray(player, amount);
		return Command.SINGLE_SUCCESS;
	}
}
