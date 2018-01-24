package org.dimdev.dimdoors.shared.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.dimdoors.shared.pockets.PocketGenerator;
import org.dimdev.dimdoors.shared.pockets.PocketTemplate;
import org.dimdev.dimdoors.shared.pockets.SchematicHandler;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.pocketlib.Pocket;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandPocket extends CommandBase {

    @Override
    public String getName() {
        return "pockets";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.pockets.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);

        // Check that the number of arguments is correct
        if (args.length < 2 || args.length > 3) {
            throw new WrongUsageException("commands.pockets.usage");
        }

        // Make sure the player is in a pocket world
        if (!ModDimensions.isDimDoorsPocketDimension(player.world)) {
            throw new CommandException("commands.generic.dimdoors.not_in_pocket_dim");
        }

        String group = args[0];
        String name = args[1];

        // Check if the schematic exists
        if (!SchematicHandler.INSTANCE.getTemplateGroups().contains(group)) {
            throw new CommandException("commands.pockets.groupnotfound", group);
        } else if (!SchematicHandler.INSTANCE.getTemplateNames(group).contains(name)) {
            throw new CommandException("commands.pockets.templatenotfound", group);
        }

        boolean setup = parseBoolean(args[3]);

        // Generate the schematic
        PocketTemplate template = SchematicHandler.INSTANCE.getTemplate(group, name);
        Pocket pocket = PocketGenerator.generatePocketFromTemplate(WorldUtils.getDim(player.world), template, null, setup);

        // Teleport the player there
        if (RiftRegistry.instance().getPocketEntrance(pocket) != null) {
            TileEntityRift entrance = (TileEntityRift) player.world.getTileEntity(RiftRegistry.instance().getPocketEntrance(pocket).getPos());
            entrance.teleportTo(player);
        } else {
            int size = (pocket.getSize() + 1) * 16;
            TeleportUtils.teleport(player, new Location(player.world, pocket.getOrigin().add(size / 2, size / 2, size / 2)));
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
