package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class StandingInAir {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("limbo_air").executes(new Command<CommandSourceStack>() {
            @Override
            public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
                var player = context.getSource().getPlayerOrException();

                System.out.println("You are within: " + player.serverLevel().getBlockState(player.blockPosition()).getBlockHolder().unwrapKey().get().location());

                return 0;
            }
        }));
    }
}
