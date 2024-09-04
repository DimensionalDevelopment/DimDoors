package org.dimdev.dimdoors.command;

import dev.architectury.event.events.common.CommandRegistrationEvent;

public final class ModCommands {
    public static void init() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, dedicated) -> {
            DimTeleportCommand.register(dispatcher);
            PocketCommand.register(dispatcher);
            StandingInAir.register(dispatcher);
        });
    }
}
