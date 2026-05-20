package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.util.schematic.SchemFixer;

public final class ModCommands {
    public static void init() {
        DimensionalDoors.getSided().registerCommands(dispatcher -> {
            DimTeleportCommand.register(dispatcher);
            PocketCommand.register(dispatcher);
            StandingInAir.register(dispatcher);
            FrayCommand.register(dispatcher);

//            dispatcher.register(Commands.literal("schem_fix").requires(so).executes(SchemFixer::main));
        });
    }
}
