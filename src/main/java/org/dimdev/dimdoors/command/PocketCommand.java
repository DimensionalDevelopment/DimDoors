package org.dimdev.dimdoors.command;

import com.flowpowered.math.vector.Vector3i;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.dimdev.dimdoors.command.arguments.GroupArugmentType;
import org.dimdev.dimdoors.command.arguments.NameArugmentType;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.pockets.SchematicHandler;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.rift.targets.EntityTarget;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.util.Location;
import org.dimdev.util.TeleportUtil;

public class PocketCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("pocket")
                .then(CommandManager.argument("group", GroupArugmentType.group())
                .then(CommandManager.argument("name", NameArugmentType.name())
                .then(CommandManager
                        .argument("setup", BoolArgumentType.bool())
                        .executes(ctx -> pocket(ctx, false))
                )
                .executes(ctx -> pocket(ctx, true)))));
    }

    private static int pocket(CommandContext<ServerCommandSource> ctx, boolean setup) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayer();

        if (!ModDimensions.isDimDoorsPocketDimension(player.world)) {
            throw new CommandException(new TranslatableText("commands.generic.dimdoors.not_in_pocket"));
        }

        String group = ctx.getArgument("group", String.class);
        String name = ctx.getArgument("name", String.class);

        if(!setup) {
            setup = BoolArgumentType.getBool(ctx, "setup");
        }

        pocket(group, name, setup, player);

        return 1;
    }

    public static void pocket(String group, String name, boolean setup, ServerPlayerEntity player) {
        try {
            PocketTemplate template = SchematicHandler.INSTANCE.getTemplate(group, name);

            Pocket pocket = PocketGenerator.generatePocketFromTemplate(player.getServerWorld(), template, null, setup);

            // Teleport the player there
            if (RiftRegistry.instance(player.world).getPocketEntrance(pocket) != null) {
                EntityTarget entrance = (EntityTarget) player.world.getBlockEntity(RiftRegistry.instance(player.world).getPocketEntrance(pocket).pos);
                if (entrance != null) {
                    entrance.receiveEntity(player, 0);
                }
            } else {
                Vector3i size = pocket.getSize().add(1, 1, 1).mul(15).div(2);
                TeleportUtil.teleport(player, new Location(player.getServerWorld(), pocket.getOrigin().add(size.getX(), size.getY(), size.getZ())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
