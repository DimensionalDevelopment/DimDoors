package com.zixiken.dimdoors.shared.commands;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.*;
import com.zixiken.dimdoors.shared.pockets.*;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import com.zixiken.dimdoors.shared.util.StringUtils;
import com.zixiken.dimdoors.shared.util.WorldUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException { // TODO: option to replace current pocket
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            if (areArgumentsValid(args, player)) {
                int dim = WorldUtils.getDim(player.world);
                if (DimDoorDimensions.isPocketDimension(dim)) {
                    PocketTemplate template = SchematicHandler.INSTANCE.getTemplate(args[0], args[1]);
                    Pocket pocket = PocketGenerator.generatePocketFromTemplate(dim, 0, template, new VirtualLocation(0, 0, 0, 0,0));
                    // TODO: options for linking back/not setting entrance
                    pocket.selectEntrance();
                    TileEntityRift entrance = (TileEntityRift) player.world.getTileEntity(pocket.getEntrance().getPos());
                    entrance.teleportTo(player);
                } else {
                    DimDoors.chat(player, "You must be in a pocket dimension to use this command!");
                }
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
