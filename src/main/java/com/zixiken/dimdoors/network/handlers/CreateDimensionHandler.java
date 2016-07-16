package com.zixiken.dimdoors.network.handlers;

import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.network.packets.CreateDimensionPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CreateDimensionHandler implements IMessageHandler<CreateDimensionPacket, IMessage> {

    public CreateDimensionHandler() {}

    @Override
    public IMessage onMessage(CreateDimensionPacket message, MessageContext ctx) {
        PocketManager.getDimwatcher().onCreated(message.getDimensionData());

        return null;
    }
}
