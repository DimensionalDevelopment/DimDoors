package com.zixiken.dimdoors.shared.commands;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import com.zixiken.dimdoors.shared.util.TeleportUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
        int dimension, x, y, z;
        int yaw = 0; // TODO: keep old yaw and pitch?
        int pitch = 0;
        try {
            dimension = Integer.parseInt(args[0]);
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
            if (args.length >= 5) yaw = Integer.parseInt(args[4]);
            if (args.length >= 6) pitch = Integer.parseInt(args[5]);
        } catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
            sender.sendMessage(new TextComponentString("[DimDoors] Incorrect usage."));
            return;
        }

        if (sender instanceof Entity) {
            TeleportUtils.teleport((Entity) sender, new Location(dimension, new BlockPos(x, y, z)), yaw, pitch);
        } else {
            DimDoors.log.info("Not executing command /" + getName() + " because it wasn't sent by a player.");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> list = new ArrayList<>();
        if (args.length < 2) { //counts an empty ("") argument as an argument as well...
            list = StringUtils.getAsStringList(DimensionManager.getIDs());
            list = StringUtils.getMatchingStrings(args[0], list, false);
        }
        return list;
    }
}
