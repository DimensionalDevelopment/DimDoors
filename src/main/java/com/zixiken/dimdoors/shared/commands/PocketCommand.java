package com.zixiken.dimdoors.shared.commands;

import com.zixiken.dimdoors.shared.*;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PocketCommand extends CommandBase {

    private final List<String> aliases;

    public PocketCommand() {
        aliases = new ArrayList<>();

        aliases.add("dimpocket");
    }

    @Override
    public String getName() {
        return "dimpocket";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "dimpocket <name>";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if(sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            BlockPos pos = player.getPosition();
            World world = player.world;

            TileEntityDimDoor newRift = (TileEntityDimDoor) world.getTileEntity(pos);
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return SchematicHandler.INSTANCE.getDungeonTemplates().stream().map(PocketTemplate::getName).collect(Collectors.toList());
    }
}
