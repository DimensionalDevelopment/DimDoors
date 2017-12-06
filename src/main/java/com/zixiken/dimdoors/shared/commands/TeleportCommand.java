package com.zixiken.dimdoors.shared.commands;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import com.zixiken.dimdoors.shared.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.DimensionManager;

public class TeleportCommand extends CommandBase {

    private final List<String> aliases;

    public TeleportCommand() {
        aliases = new ArrayList<>();
        aliases.add("dimteleport");
    }

    @Override
    public String getName() {
        return "dimteleport";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "dimteleport <dimension>";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
            sender.sendMessage(new TextComponentString("[DimDoors] Incorrect usage."));
            return;
        }

        if (sender instanceof EntityPlayerMP) {
            server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) sender, id, TeleporterDimDoors.instance());
        } else {
            DimDoors.log("Not executing command /" + getName() + " because it wasn't sent by a player.");
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
