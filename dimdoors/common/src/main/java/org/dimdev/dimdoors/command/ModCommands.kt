package org.dimdev.dimdoors.command

import org.dimdev.dimcore.DimCore.platform

object ModCommands {
    fun register() {
        platform.registerCommands { dispatcher ->
            DimTeleportCommand.register(dispatcher)
            PocketCommand.register(dispatcher)
            StandingInAir.register(dispatcher)
            //FrayCommand.register(dispatcher); TODO: Finish Fray
            //dispatcher.register(Commands.literal("schem_fix").requires(so).executes(SchemFixer::main));
        }
    }
}
