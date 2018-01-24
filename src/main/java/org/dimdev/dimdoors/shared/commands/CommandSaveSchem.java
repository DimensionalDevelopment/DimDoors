package org.dimdev.dimdoors.shared.commands;

import com.flowpowered.math.vector.Vector3i;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.shared.pockets.SchematicHandler;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;
import org.dimdev.pocketlib.WorldProviderPocket;

public class CommandSaveSchem extends CommandBase {

    @Override
    public String getName() {
        return "saveschem";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.saveschem.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);

        if (args.length != 1) {
            throw new WrongUsageException("commands.saveschem.usage");
        }

        if (!(player.world.provider instanceof WorldProviderPocket))
            throw new CommandException("commands.generic.dimdoors.not_in_pocket");
        Pocket pocket = PocketRegistry.instance(player.dimension).getPocketAt(player.getPosition());
        if (pocket == null) throw new CommandException("commands.generic.dimdoors.not_in_pocket");

        Schematic schematic = Schematic.createFromWorld(player.world, toVector3i(pocket.getOrigin()), toVector3i(pocket.getOrigin()).add(Vector3i.from((pocket.getSize() + 1) * 16 - 1)));
        schematic.name = args[0];
        schematic.author = player.getName();

        SchematicHandler.INSTANCE.saveSchematic(schematic, args[0]);
        notifyCommandListener(sender, this, "commands.saveschem.success", args[0]);
    }

    private Vector3i toVector3i(BlockPos pos) {
        return Vector3i.from(pos.getX(), pos.getY(), pos.getZ());
    }
}
