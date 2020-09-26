package org.dimdev.dimdoors.command;

import org.dimdev.dimdoors.ModConfig;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class DimdoorsConfigCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("dimdoorsconfig")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(
                                literal("load")
                                        .executes(ctx -> ModConfig.deserialize())
                        )
        );
    }
}
