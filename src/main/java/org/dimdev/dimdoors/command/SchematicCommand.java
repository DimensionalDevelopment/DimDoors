package org.dimdev.dimdoors.command;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.dimdev.Generator;
import org.dimdev.dimcore.schematic.Schematic;
import org.dimdev.dimcore.schematic.SchematicConverter;

import java.io.InputStream;

public class SchematicCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("schematic")
                .then(CommandManager.literal("place")
                        .then(CommandManager.argument("schematic_name", StringArgumentType.string())
                                .executes(ctx -> {
                                            SchematicConverter.reloadConversions();
                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                            String id = StringArgumentType.getString(ctx, "schematic_name");

                                            try (InputStream in = Generator.class.getResourceAsStream("/data/dimdoors/schematic/ruins/" + id + ".schem")) {
                                                Schematic.fromTag(NbtIo.readCompressed(in))
                                                        .place(
                                                                player.world,
                                                                (int) player.getPos().x,
                                                                (int) player.getPos().y,
                                                                (int) player.getPos().z
                                                        );
                                            } catch (Throwable t) {
                                                t.printStackTrace();
                                            }

                                            System.out.println(id + " placed");

                                            return 1;
                                        }
                                )
                        )
                )
        );
    }
}
