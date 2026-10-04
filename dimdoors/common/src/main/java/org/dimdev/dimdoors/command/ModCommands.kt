package org.dimdev.dimdoors.command

import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.command.Vec3ValueArgument

object ModCommands {
    fun register() {
        platform.registerArgumentType(ResourceLocation.fromNamespaceAndPath("dimcore", "vec3"), Vec3ValueArgument::class.java) { Vec3ValueArgument }
        platform.registerCommands { dispatcher ->
            DimTeleportCommand.register(dispatcher)
            PocketCommand.register(dispatcher)
            //FrayCommand.register(dispatcher); TODO: Finish Fray
            //dispatcher.register(Commands.literal("schem_fix").requires(so).executes(SchemFixer::main));
        }
    }
}
