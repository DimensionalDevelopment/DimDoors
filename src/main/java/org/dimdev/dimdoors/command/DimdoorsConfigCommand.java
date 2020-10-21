package org.dimdev.dimdoors.command;

import com.mojang.brigadier.CommandDispatcher;
import org.dimdev.dimdoors.ModConfig;

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
