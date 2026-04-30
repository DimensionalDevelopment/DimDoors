package org.dimdev.dimdoors.command;

import org.dimdev.dimdoors.DimensionalDoors;

public final class ModCommands {
    public static void init() {
        DimensionalDoors.getSided().registerCommands(dispatcher -> {
            DimTeleportCommand.register(dispatcher);
            PocketCommand.register(dispatcher);
            StandingInAir.register(dispatcher);
        });
    }
}
