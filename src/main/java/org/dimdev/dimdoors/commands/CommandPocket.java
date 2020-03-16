//package org.dimdev.dimdoors.commands;
//
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.command.WrongUsageException;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.BlockPos;
//import org.dimdev.util.Location;
//import org.dimdev.util.TeleportUtil;
//import org.dimdev.util.WorldUtils;
//import org.dimdev.dimdoors.pockets.PocketGenerator;
//import org.dimdev.dimdoors.pockets.PocketTemplate;
//import org.dimdev.dimdoors.pockets.SchematicHandler;
//import org.dimdev.dimdoors.rift.targets.EntityTarget;
//import org.dimdev.dimdoors.rift.registry.RiftRegistry;
//import org.dimdev.dimdoors.world.ModDimensions;
//import org.dimdev.pocketlib.Pocket;
//
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class CommandPocket extends CommandBase {
//
//    @Override
//    public String getName() {
//        return "pocket";
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "commands.pocket.usage";
//    }
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
//
//        // Check that the number of arguments is correct
//        if (args.length < 2 || args.length > 3) {
//            throw new WrongUsageException("commands.pocket.usage");
//        }
//
//        // Make sure the player is in a pocket world
//        if (!ModDimensions.isDimDoorsPocketDimension(player.world)) {
//            throw new CommandException("commands.generic.dimdoors.not_in_pocket_dim");
//        }
//
//        String group = args[0];
//        String name = args[1];
//
//        // Check if the schematic exists
//        if (!SchematicHandler.INSTANCE.getTemplateGroups().contains(group)) {
//            throw new CommandException("commands.pocket.group_not_found", group);
//        } else if (!SchematicHandler.INSTANCE.getTemplateNames(group).contains(name)) {
//            throw new CommandException("commands.pocket.template_not_found", name);
//        }
//
//        boolean setup = args.length < 3 || parseBoolean(args[2]);
//
//        // Generate the schematic
//        PocketTemplate template = SchematicHandler.INSTANCE.getTemplate(group, name);
//        Pocket pocket = PocketGenerator.generatePocketFromTemplate(WorldUtils.getDim(player.world), template, null, setup);
//
//        // Teleport the player there
//        if (RiftRegistry.instance().getPocketEntrance(pocket) != null) {
//            EntityTarget entrance = (EntityTarget) player.world.getBlockEntity(RiftRegistry.instance().getPocketEntrance(pocket).pos);
//            entrance.receiveEntity(player, 0, 0);
//        } else {
//            int size = (pocket.getSize() + 1) * 16;
//            TeleportUtil.teleport(player, new BlockPos((pocket.box.minX + pocket.box.maxX)/2, (pocket.box.minY + pocket.box.maxY)/2, (pocket.box.minZ + pocket.box.maxZ)/2)));
//        }
//    }
//
//    @Override
//    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,  BlockPos targetPos) {
//        List<String> list = new ArrayList<>();
//        switch (args.length) {
//            case 1:
//                list = new ArrayList<>(SchematicHandler.INSTANCE.getTemplateGroups());
//                break;
//            case 2:
//                list = new ArrayList<>(SchematicHandler.INSTANCE.getTemplateNames(args[0]));
//                break;
//            case 3:
//                list.add("true");
//                list.add("false");
//                break;
//        }
//        return list.stream()
//                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
//                .collect(Collectors.toList());
//    }
//}
