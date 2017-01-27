package com.zixiken.dimdoors;

import com.zixiken.dimdoors.shared.Location;
import com.zixiken.dimdoors.shared.TeleportHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import scala.Int;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jared Johnson on 1/26/2017.
 */
public class TeleportCommand extends CommandBase {
    private final List aliases;

    public TeleportCommand()
    {
        aliases = new ArrayList();

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
            server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) sender, Integer.parseInt(args[0]), new TeleportHelper(new Location(id, 0,300,0)));

        }
    }
}
