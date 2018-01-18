package org.dimdev.dimdoors.shared.commands;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.pockets.*;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.dimdoors.shared.world.ModDimensions;
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
import java.util.stream.Collectors;

public class CommandPocket extends CommandBase {

    private final List<String> aliases;

    public CommandPocket() {
        aliases = new ArrayList<>();
        aliases.add("pocket");
    }

    @Override
    public String getName() {
        return "pocket";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "pocket <group> <name> [setup]";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException { // TODO: more pocket commands (replace pocket, get ID, teleport to pocket, etc.)
        // Check that the number of arguments is correct
        if (args.length < 2 || args.length > 3) {
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
            if (!ModDimensions.isDimDoorsPocketDimension(player.world)) {
                DimDoors.chat(player, "You must be in a pocket dimension to use this command!");
                return;
            }

            // Check if the schematic exists
            if (!SchematicHandler.INSTANCE.getTemplateGroups().contains(group)) {
                DimDoors.chat(player, "Group " + group + " not found");
                return;
            } else if (!SchematicHandler.INSTANCE.getTemplateNames(group).contains(name)) {
                DimDoors.chat(player, "Schematic " + name + " not found in group " + group);
                return;
            }

            // Generate the schematic
            PocketTemplate template = SchematicHandler.INSTANCE.getTemplate(group, name);
            Pocket pocket = PocketGenerator.generatePocketFromTemplate(WorldUtils.getDim(player.world), template, null);
            if (setup) pocket.setup();

            // Teleport the player there
            if (pocket.getEntrance() != null) {
                TileEntityRift entrance = (TileEntityRift) player.world.getTileEntity(pocket.getEntrance().getPos());
                entrance.teleportTo(player);
            } else {
                TeleportUtils.teleport(player, new Location(player.world, pocket.getOrigin().add(30, 30, 30)));
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
                list = new ArrayList<>(SchematicHandler.INSTANCE.getTemplateGroups());
                break;
            case 2:
                list = new ArrayList<>(SchematicHandler.INSTANCE.getTemplateNames(args[0]));
                break;
            case 3:
                list.add("true");
                list.add("false");
                break;
        }
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
