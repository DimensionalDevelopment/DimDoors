package org.dimdev.dimdoors.shared.commands;

import com.flowpowered.math.vector.Vector3i;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockFabric;
import org.dimdev.dimdoors.shared.pockets.SchematicHandler;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPocket;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;

import java.util.ArrayList;
import java.util.List;

public class CommandFabricConvert extends CommandBase {
    private final List<String> aliases;

    public CommandFabricConvert() {
        aliases = new ArrayList<>();
        aliases.add("fabricconvert");
    }

    @Override
    public String getName() {
        return "fabricconvert";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "fabricconvert";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // Execute only if it's a player
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);

            if (!(player.world.provider instanceof WorldProviderPocket)) {
                DimDoors.chat(player, "Current Dimension isn't a pocket dimension");
                return;
            }

            Pocket pocket = PocketRegistry.instance(player.dimension).getPocketAt(player.getPosition());

            BlockPos origin = pocket.getOrigin();
            int size = (pocket.getSize() + 1) * 16 - 1;

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    for (int z = 0; z < size; z++) {
                        IBlockState state = player.world.getBlockState(new BlockPos(origin.getX() + x, origin.getY() + y, origin.getZ() + z));

                        if (state.getBlock() instanceof BlockFabric) {
                            player.world.setBlockState(origin, state.withProperty(BlockFabric.COLOR, EnumDyeColor.BLACK));
                        }
                    }
                }
            }

            DimDoors.chat(player, "All fabric's of reality have been converted to black.");
        } else {
            DimDoors.log.info("Not executing command /" + getName() + " because it wasn't sent by a player.");
        }
    }
}
