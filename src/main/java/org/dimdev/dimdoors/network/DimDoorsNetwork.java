package org.dimdev.dimdoors.network;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.network.handlers.*;
import org.dimdev.dimdoors.network.packets.*;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

@ChannelHandler.Sharable
public class DimDoorsNetwork extends SimpleNetworkWrapper {

    private static final DimDoorsNetwork INSTANCE = new DimDoorsNetwork(DimDoors.modid);

    public DimDoorsNetwork(String channelName) {
        super(channelName);
    }

    public static void init() {
        INSTANCE.registerMessage(ClientJoinHandler.class, ClientJoinPacket.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(CreateDimensionHandler.class, CreateDimensionPacket.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(DeleteDimensionHandler.class, DeleteDimensionPacket.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(CreateLinkHandler.class, CreateLinkPacket.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(DeleteLinkHandler.class, DeleteLinkPacket.class, 4, Side.CLIENT);
        INSTANCE.registerMessage(UpdateLinkHandler.class, UpdateLinkPacket.class, 5, Side.CLIENT);
    }

    public static void sendToAllPlayers(IMessage message) {
        INSTANCE.sendToAll(message);
    }

    public static void sendToPlayer(IMessage message, EntityPlayer player) {
        if (player instanceof EntityPlayerMP)
            INSTANCE.sendTo(message, (EntityPlayerMP) player);
    }
}