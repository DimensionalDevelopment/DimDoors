//package org.dimdev.dimdoors.commands;
//
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.command.WrongUsageException;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.Vec3i;
//import org.dimdev.util.schem.Schematic;
//import org.dimdev.dimdoors.pockets.SchematicHandler;
//import org.dimdev.pocketlib.Pocket;
//import org.dimdev.pocketlib.PocketRegistry;
//import org.dimdev.pocketlib.PocketWorldDimension;
//
//public class CommandSaveSchem extends CommandBase {
//
//    @Override
//    public String getName() {
//        return "saveschem";
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "commands.saveschem.usage";
//    }
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
//
//        if (args.length != 1) {
//            throw new WrongUsageException("commands.saveschem.usage");
//        }
//
//        if (!(player.world.dimension instanceof PocketWorldDimension))
//            throw new CommandException("commands.generic.dimdoors.not_in_pocket");
//        Pocket pocket = PocketRegistry.instance(player.dimension).getPocketAt(player.getPosition());
//        if (pocket == null) throw new CommandException("commands.generic.dimdoors.not_in_pocket");
//
//        int size = (pocket.getSize() + 1) * 16 - 1;
//        Schematic schematic = Schematic.createFromWorld(player.world, pocket.getOrigin(), pocket.getOrigin().add(new Vec3i(size, size, size)));
//        schematic.name = args[0];
//        schematic.author = player.getName();
//
//        SchematicHandler.INSTANCE.saveSchematicForEditing(schematic, args[0]);
//        notifyCommandListener(sender, this, "commands.saveschem.success", args[0]);
//    }
//}
