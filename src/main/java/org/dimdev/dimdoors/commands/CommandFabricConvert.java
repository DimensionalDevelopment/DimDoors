//package org.dimdev.dimdoors.commands;
//
//import net.minecraft.block.state.BlockState;
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.item.DyeColor;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.BlockPos;
//import org.dimdev.dimdoors.block.FabricBlock;
//import org.dimdev.pocketlib.Pocket;
//import org.dimdev.pocketlib.PocketRegistry;
//import org.dimdev.pocketlib.PocketWorldDimension;
//
//public class CommandFabricConvert extends CommandBase {
//
//    @Override
//    public String getName() {
//        return "fabricconvert";
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "commands.fabricconvert.usage";
//    }
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
//
//        if (!(player.world.dimension instanceof PocketWorldDimension)) throw new CommandException("commands.generic.dimdoors.not_in_pocket");
//        Pocket pocket = PocketRegistry.instance(player.dimension).getPocketAt(player.getPosition());
//        if (pocket == null) throw new CommandException("commands.generic.dimdoors.not_in_pocket");
//
//        BlockPos origin = pocket.getOrigin();
//        int size = (pocket.getSize() + 1) * 16 - 1;
//
//        for (int x = 0; x < size; x++) {
//            for (int y = 0; y < size; y++) {
//                for (int z = 0; z < size; z++) {
//                    BlockState state = player.world.getBlockState(new BlockPos(origin.getX() + x, origin.getY() + y, origin.getZ() + z));
//
//                    if (state.getBlock() instanceof FabricBlock) {
//                        player.world.setBlockState(origin, state.withProperty(FabricBlock.COLOR, DyeColor.BLACK));
//                    }
//                }
//            }
//        }
//
//        notifyCommandListener(sender, this, "commands.fabricconvert.success");
//    }
//}
