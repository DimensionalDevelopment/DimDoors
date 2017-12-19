package com.zixiken.dimdoors.shared.commands;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.*;
import com.zixiken.dimdoors.shared.pockets.*;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.util.StringUtils;
import com.zixiken.dimdoors.shared.util.TeleportUtils;
import com.zixiken.dimdoors.shared.util.WorldUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
        return "dimpocket <group> <name> [setup]";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException { // TODO: option to replace current pocket
        // Check correct number of arguments
        if (args.length > 2 || args.length > 3) {
            sender.sendMessage(new TextComponentString("[DimDoors] Usage: /" + getUsage(sender)));
            return;
        }
        String group = args[0];
        String name = args[1];
        boolean setup = true;
        if (args.length >= 3) {
            switch (args[2]) {
                case "true":
                    setup = true;
                    break;
                case "false":
                    setup = false;
                    break;
                default:
                    sender.sendMessage(new TextComponentString("[DimDoors] Usage: /" + getUsage(sender)));
                    return;
            }
        }

        // Execute only if it's a player
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            // Make sure the player is in a pocket world
            if (!DimDoorDimensions.isPocketDimension(WorldUtils.getDim(player.world))) {
                DimDoors.chat(player, "You must be in a pocket dimension to use this command!");
                return;
            }

            // Check if the schematic exists
            if (!SchematicHandler.INSTANCE.getTemplateGroups().contains(name)) {
                DimDoors.chat(player, "Group " + group + " not found");
                return;
            } else if (!SchematicHandler.INSTANCE.getTemplateNames(name).contains(group)) {
                DimDoors.chat(player, "Schematic " + name + " not found in group " + group);
                return;
            }

            // Generate the schematic and teleport the player to it
            DimDoors.chat(player, "Generating schematic " + args[1]);
            PocketTemplate template = SchematicHandler.INSTANCE.getTemplate(args[0], args[1]);
            Pocket pocket = PocketGenerator.generatePocketFromTemplate(WorldUtils.getDim(player.world), template, new VirtualLocation(0, 0, 0, 0,0));
            if (setup) pocket.setup();
            if (pocket.getEntrance() != null) {
                TileEntityRift entrance = (TileEntityRift) player.world.getTileEntity(pocket.getEntrance().getPos());
                entrance.teleportTo(player);
            } else {
                TeleportUtils.teleport(player, new Location(player.world, pocket.getX(), 0, pocket.getZ()), 0, 0);
            }
        } else {
            DimDoors.log.info("Not executing command /" + getName() + " because it wasn't sent by a player.");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1:
                list = SchematicHandler.INSTANCE.getTemplateGroups();
                break;
            case 2:
                list = SchematicHandler.INSTANCE.getTemplateNames(args[0]);
                break;
            case 3:
                list.add("true");
                list.add("false");
                break;
        }
        return StringUtils.getMatchingStrings(args[0], list, false);
    }
}
