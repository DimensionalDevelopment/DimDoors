package org.dimdev.dimdoors.shared.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.DimensionManager;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.TeleportUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandDimTeleport extends CommandBase {

    @Override
    public String getName() {
        return "dimteleport";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.dimteleport.usage";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("dteleport", "dtp");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);

        // Check that the number of arguments is correct
        if (args.length < 4 || args.length > 6) {
            throw new WrongUsageException("commands.dimteleport.usage");
        }

        int dimension = parseInt(args[0]);

        Vec3d senderPos = sender.getPositionVector();
        CoordinateArg x = parseCoordinate(senderPos.x, args[1], true);
        CoordinateArg y = parseCoordinate(senderPos.y, args[2], false);
        CoordinateArg z = parseCoordinate(senderPos.z, args[3], true);

        CoordinateArg yaw = parseCoordinate(player.rotationYaw, args.length > 4 ? args[4] : "~", false);
        CoordinateArg pitch = parseCoordinate(player.rotationPitch, args.length > 5 ? args[5] : "~", false);

        TeleportUtils.teleport(player, new Location(dimension, new BlockPos(x.getResult(), y.getResult(), z.getResult())), (float) yaw.getResult(), (float) pitch.getResult());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return Arrays.stream(DimensionManager.getStaticDimensionIDs())
                    .map(Object::toString)
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            return args.length > 1 && args.length <= 4 ? getTabCompletionCoordinate(args, 1, targetPos) : Collections.emptyList();
        }
    }
}
