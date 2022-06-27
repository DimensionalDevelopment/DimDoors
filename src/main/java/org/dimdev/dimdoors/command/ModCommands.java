package org.dimdev.dimdoors.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class ModCommands {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> {
            DimTeleportCommand.register(dispatcher);
            PocketCommand.register(dispatcher);
        });
    }
}
