package com.zixiken.dimdoors.shared.commands;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.*;
import com.zixiken.dimdoors.shared.pockets.*;
import com.zixiken.dimdoors.shared.util.StringUtils;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
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
        return "dimpocket <group> <name>";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            if (areArgumentsValid(args, player)) {
                DimDoors.log(getClass(), "Executing command");

                BlockPos pos = player.getPosition();
                World world = player.world;
                Location origLoc = new Location(world, pos);

                int dimID = origLoc.getDimensionID();
                if (DimDoorDimensions.isPocketDimensionID(dimID)) {
                    int pocketID = PocketRegistry.getForDim(dimID).getIDFromLocation(origLoc);
                    Pocket oldPocket = PocketRegistry.getForDim(dimID).getPocket(pocketID);
                }

                PocketTemplate template = SchematicHandler.INSTANCE.getTemplate(args[0], args[1]);
                Pocket pocket = PocketGenerator.generatePocketFromTemplate(dimID, 0, template, 0);
                int entranceDoorID = pocket.getEntranceRiftID();
                //RiftRegistry.INSTANCE.setLastGeneratedEntranceDoorID(entranceDoorID);
                // TODO: teleport the player
            }
        } else {
            DimDoors.log("Not executing command /" + getName() + " because it wasn't sent by a player.");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> list = new ArrayList<>();
        if (args.length < 2) { //counts an empty ("") argument as an argument as well...
            list = SchematicHandler.INSTANCE.getTemplateGroups();
            list = StringUtils.getMatchingStrings(args[0], list, false);
        } else if (args.length == 2) {
            list = SchematicHandler.INSTANCE.getTemplateNames(args[0]);
            list = StringUtils.getMatchingStrings(args[1], list, false);
        }
        return list;
    }

    private boolean areArgumentsValid(String[] args, EntityPlayerMP player) {
        if (args.length < 2) {
            DimDoors.chat(player, "Too few arguments.");
            return false;
        } else if (args.length > 2) {
            DimDoors.chat(player, "Too many arguments.");
            return false;
        } else { //exactly 2 arguments
            if (!SchematicHandler.INSTANCE.getTemplateGroups().contains(args[0])) {
                DimDoors.chat(player, "Group not found.");
                return false;
            } else if (!SchematicHandler.INSTANCE.getTemplateNames(args[0]).contains(args[1])) {
                DimDoors.chat(player, "Schematic not found.");
                return false;
            } else {
                DimDoors.chat(player, "Generating schematic " + args[1]);
                return true;
            }
        }
    }
}
