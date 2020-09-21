package org.dimdev.dimdoors.command;

import java.io.IOException;
import java.io.InputStream;

import org.dimdev.dimcore.schematic.v2.Schematic;
import org.dimdev.dimcore.schematic.v2.SchematicPlacer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SchematicV2Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("schematicv2")
                // TODO: Create an argument type for this
                .then(literal("ruins")
                        .then(argument("schematic_name", StringArgumentType.string())
                                .executes((ctx) -> place(ctx, "ruins"))
                        )
                )
                .then(literal("blank")
                        .then(argument("schematic_name", StringArgumentType.string())
                                .executes((ctx) -> place(ctx, "blank"))
                        )
                )
                .then(literal("nether")
                        .then(argument("schematic_name", StringArgumentType.string())
                                .executes((ctx) -> place(ctx, "nether"))
                        )
                )
                .then(literal("private")
                        .then(argument("schematic_name", StringArgumentType.string())
                                .executes((ctx) -> place(ctx, "private"))
                        )
                )
                .then(literal("public")
                        .then(argument("schematic_name", StringArgumentType.string())
                                .executes((ctx) -> place(ctx, "public"))
                        )
                )
        );
    }

    private static int place(CommandContext<ServerCommandSource> ctx, String namespace) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        String id = StringArgumentType.getString(ctx, "schematic_name");

        try (InputStream in = SchematicCommand.class.getResourceAsStream("/data/dimdoors/pockets/schematic/v2/" + namespace + "/" + id + ".schem")) {
            SchematicPlacer.place(
                    Schematic.fromTag(NbtIo.readCompressed(in)),
                    ctx.getSource().getWorld(),
                    ctx.getSource().getPlayer().getBlockPos()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(id + " placed");
        return 1;
    }
}
