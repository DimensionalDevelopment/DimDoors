package com.zixiken.dimdoors.network.handlers;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.NewDimData;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.network.packets.ClientJoinPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientJoinHandler implements IMessageHandler<ClientJoinPacket, IMessage> {

    public ClientJoinHandler() {}

    @Override
    public IMessage onMessage(ClientJoinPacket message, MessageContext ctx) {
        EntityPlayer player = DimDoors.proxy.getMessagePlayer(ctx);
        NewDimData dimensionData = PocketManager.getDimensionData(player.worldObj);

        if (dimensionData.isPocketDimension())
            player.worldObj.provider.registerWorld(player.worldObj);

        return null;
    }
}
