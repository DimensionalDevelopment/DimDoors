package org.dimdev.dimdoors.command;

import java.io.IOException;
import java.io.InputStream;

import org.dimdev.dimcore.schematic.Schematic;
import org.dimdev.dimcore.schematic.SchematicConverter;
import org.dimdev.dimdoors.command.arguments.SchematicNamespaceArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SchematicCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("schematicold")
                .then(literal("place")
                        .then(argument("namespace", new SchematicNamespaceArgumentType())
                                .then(argument("schematic_name", StringArgumentType.string())
                                        .executes(ctx -> {
                                                    SchematicConverter.reloadConversions();
                                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                    String id = StringArgumentType.getString(ctx, "schematic_name");
                                                    String ns = SchematicNamespaceArgumentType.getValue(ctx, "namespace");
                                                    try (InputStream in = SchematicCommand.class.getResourceAsStream("/data/dimdoors/pockets/schematic/" + ns + "/" + id + ".schem")) {
                                                        Schematic.fromTag(NbtIo.readCompressed(in))
                                                                .place(
                                                                        player.world,
                                                                        (int) player.getPos().x,
                                                                        (int) player.getPos().y,
                                                                        (int) player.getPos().z
                                                                );
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    System.out.println(id + " placed");

                                                    return 1;
                                                }
                                        )
                                )
                        )
                )
        );
    }
}