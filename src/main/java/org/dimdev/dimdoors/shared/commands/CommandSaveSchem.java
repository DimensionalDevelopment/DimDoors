package org.dimdev.dimdoors.shared.commands;

import com.flowpowered.math.vector.Vector3i;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.pockets.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketRegistry;
import org.dimdev.dimdoors.shared.pockets.SchematicHandler;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPocket;

import java.util.ArrayList;
import java.util.List;

public class CommandSaveSchem extends CommandBase {
    private final List<String> aliases;

    public CommandSaveSchem() {
        aliases = new ArrayList<>();
        aliases.add("saveschem");
    }

    @Override
    public String getName() {
        return "saveschem";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "saveschem <name>";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException { // TODO: more pocket commands (replace pocket, get ID, teleport to pocket, etc.)
        // Check that the number of arguments is correct
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString("[DimDoors] Usage: /" + getUsage(sender)));
            return;
        }

        // Execute only if it's a player
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);

            if (!(player.world.provider instanceof WorldProviderPocket)) {
                DimDoors.chat(player, "Current Dimension isn't a pocket dimension");
                return;
            }

            Pocket pocket = PocketRegistry.instance(player.dimension).getPocketAt(player.getPosition());
            Schematic schematic = Schematic.createFromWorld(player.world, toVector3i(pocket.getOrigin()), toVector3i(pocket.getOrigin()).add(Vector3i.from((pocket.getSize() + 1) * 16 - 1)));
            schematic.name = args[0];
            schematic.author = player.getName();

            SchematicHandler.INSTANCE.saveSchematic(schematic, args[0]);
            DimDoors.chat(player, "Pocket " + args[0] + " has been saved.");
        } else {
            DimDoors.log.info("Not executing command /" + getName() + " because it wasn't sent by a player.");
        }
    }

    private Vector3i toVector3i(BlockPos pos) {
        return Vector3i.from(pos.getX(), pos.getY(), pos.getZ());
    }
}
