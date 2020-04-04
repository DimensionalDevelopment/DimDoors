package org.dimdev.dimdoors.commands;


import com.google.gson.Gson;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.command.arguments.*;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.dimdev.util.TeleportUtil;

public class CommandDimTeleport {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dimteleport")
                .then(CommandManager
                        .argument("dimension", DimensionArgumentType.dimension())
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            return teleport(player, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), player.getPos(), player.getYaw(1.0f));
                        }))
                .then(CommandManager
                        .argument("coordinates", Vec3ArgumentType.vec3())
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            return teleport(player, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), Vec3ArgumentType.getVec3(ctx, "coordinates"), player.getYaw(1.0f));
                        }))
                .then(CommandManager
                        .argument("yaw", FloatArgumentType.floatArg())
                        .executes(ctx -> teleport(ctx.getSource().getPlayer(), DimensionArgumentType.getDimensionArgument(ctx, "dimension"), Vec3ArgumentType.getVec3(ctx, "coordinates"), FloatArgumentType.getFloat(ctx, "yaw")))
                )
        );

        Gson gson = new Gson();

        NbtOps nbtOps = NbtOps.INSTANCE;
        JsonOps jsonOps = JsonOps.INSTANCE;


    }

    private static int teleport(ServerPlayerEntity player, DimensionType dimension, Vec3d coordinates, float yaw) {
        try {
            TeleportUtil.teleport(player, dimension, coordinates, yaw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }
//
//    @Override1
//    public String getName() {
//        return "dimteleport";
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "commands.dimteleport.usage";
//    }
//
//    @Override
//    public List<String> getAliases() {
//        return Arrays.asList("dteleport", "dtp");
//    }
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
//
//        // Check that the number of arguments is correct
//        if (args.length < 4 || args.length > 6) {
//            throw new WrongUsageException("commands.dimteleport.usage");
//        }
//
//        int dimension = parseInt(args[0]);
//
//        Vec3d senderPos = sender.getPositionVector();
//        CoordinateArgument x = parseCoordinate(senderPos.x, args[1], true);
//        CoordinateArgument y = parseCoordinate(senderPos.y, args[2], false);
//        CoordinateArgument z = parseCoordinate(senderPos.z, args[3], true);
//
//        CoordinateArgument yaw = parseCoordinate(player.rotationYaw, args.length > 4 ? args[4] : "~", false);
//        CoordinateArgument pitch = parseCoordinate(player.rotationPitch, args.length > 5 ? args[5] : "~", false);
//
//        TeleportUtil.teleport(player, new Location(dimension, new BlockPos(x.getResult(), y.getResult(), z.getResult())), (float) yaw.getResult(), (float) pitch.getResult());
//    }
//
//    @Override
//    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,  BlockPos targetPos) {
//        if (args.length == 1) {
//            return Arrays.stream(DimensionManager.getStaticDimensionIDs())
//                    .map(Object::toString)
//                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
//                    .collect(Collectors.toList());
//        } else {
//            return args.length > 1 && args.length <= 4 ? getTabCompletionCoordinate(args, 1, targetPos) : Collections.emptyList();
//        }
//    }
}
