package org.dimdev.dimdoors.command;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public final class ModCommands {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated)->{
            DimTeleportCommand.register(dispatcher);
            SchematicCommand.register(dispatcher);
            PocketCommand.register(dispatcher);
            SaveSchemCommand.register(dispatcher);
        });
    }
}
