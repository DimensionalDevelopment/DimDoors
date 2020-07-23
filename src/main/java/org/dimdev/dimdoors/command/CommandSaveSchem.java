package org.dimdev.dimdoors.command;

import com.flowpowered.math.vector.Vector3i;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3i;
import org.dimdev.dimdoors.pockets.SchematicHandler;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.PocketRegistry;
import org.dimdev.dimcore.schematic.Schematic;

public class CommandSaveSchem {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("saveschem")
                .then(CommandManager
                        .argument("name", StringArgumentType.string())
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            if(!ModDimensions.isDimDoorsPocketDimension(player.world)) {
                                throw new CommandException(new TranslatableText("commands.generic.dimdoors.not_in_pocket"));
                            }

                            Pocket pocket = PocketRegistry.instance(player.getServerWorld()).getPocketAt(player.getBlockPos());
                            if (pocket == null) throw new CommandException(new TranslatableText("commands.generic.dimdoors.not_in_pocket"));

                            Vector3i size = pocket.getSize().add(1,1,1).mul(16).sub(1,1,1);
                            Schematic schematic = Schematic.createFromWorld(player.world, pocket.getOrigin(), pocket.getOrigin().add(new Vec3i(size.getX(), size.getY(), size.getZ())));

                            String name = StringArgumentType.getString(ctx, "name");

                            schematic.name = name;
                            schematic.author = player.getName().getString();

                            SchematicHandler.INSTANCE.saveSchematicForEditing(schematic, name);

                            ctx.getSource().sendError(new TranslatableText("commands.saveschem.success"));
                            return 1;
                        }))
        );
    }
}
