package org.dimdev.dimdoors.shared.commands;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.ddutils.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.dimdev.ddutils.TeleportUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.DimensionManager;

public class CommandDimTeleport extends CommandBase { // TODO: localization

    private final List<String> aliases;

    public CommandDimTeleport() {
        aliases = new ArrayList<>();
        aliases.add("dimteleport");
        aliases.add("dteleport");
        aliases.add("dtp");
    }

    @Override
    public String getName() {
        return "dimteleport";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "dimteleport <dimension> <x> <y> <z> [yaw] [pitch]";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // Check correct number of arguments
        if (args.length < 4 || args.length > 6) {
            sender.sendMessage(new TextComponentString("[DimDoors] Usage: /" + getUsage(sender)));
            return;
        }

        // Parse arguments
        int dimension, x, y, z;
        int yaw = 0;
        int pitch = 0;
        try {
            dimension = Integer.parseInt(args[0]);
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
            if (args.length >= 5) yaw = Integer.parseInt(args[4]);
            if (args.length >= 6) pitch = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponentString("[DimDoors] Usage: /" + getUsage(sender)));
            return;
        }

        // Teleport if it's a player
        if (sender instanceof Entity) {
            TeleportUtils.teleport((Entity) sender, new Location(dimension, new BlockPos(x, y, z)), yaw, pitch);
        } else {
            DimDoors.log.info("Not executing command /" + getName() + " because it wasn't sent by a player.");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return Arrays.stream(DimensionManager.getIDs())
                    .map(Object::toString)
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
