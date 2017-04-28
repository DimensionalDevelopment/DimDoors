package com.zixiken.dimdoors.shared.commands;

import com.zixiken.dimdoors.shared.PocketTemplate;
import com.zixiken.dimdoors.shared.SchematicHandler;
import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

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
        int id = Integer.parseInt(args[0]);

        if (sender instanceof EntityPlayerMP) {
            server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) sender, id, TeleporterDimDoors.instance());
        }
    }
}
