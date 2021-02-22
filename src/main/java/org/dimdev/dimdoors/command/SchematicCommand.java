package org.dimdev.dimdoors.command;

import java.io.IOException;
import java.io.InputStream;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.command.arguments.SchematicNamespaceArgumentType;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;

import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SchematicCommand {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("schematicv2")
                .then(argument("namespace", new SchematicNamespaceArgumentType())
                        .then(argument("schematic_name", StringArgumentType.string())
                                .executes(SchematicCommand::place)
                        )
                )
        );
    }

    private static int place(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        String id = StringArgumentType.getString(ctx, "schematic_name");
        String ns = SchematicNamespaceArgumentType.getValue(ctx, "namespace");

        try (InputStream in = DimensionalDoorsInitializer.class.getResourceAsStream("/resourcepacks/default_pockets/data/dimdoors/pockets/schematic/v2/" + ns + "/" + id + ".schem")) {
            SchematicPlacer.place(
                    Schematic.fromTag(NbtIo.readCompressed(in)),
                    ctx.getSource().getWorld(),
                    ctx.getSource().getPlayer().getBlockPos(),
					false
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new SimpleCommandExceptionType(new TranslatableText("command.dimdoors.schematicv2.unknownSchematic", id, ns)).create();
        }

        LOGGER.info(String.format("Placed schematic %s from namespace %s at %s in world %s", id, ns, player.getBlockPos(), player.getServerWorld().getRegistryKey().getValue()));
        return 1;
    }
}
