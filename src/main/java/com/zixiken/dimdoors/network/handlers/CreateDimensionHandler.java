package com.zixiken.dimdoors.network.handlers;

import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.network.packets.CreateDimensionPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class CreateDimensionHandler implements IMessageHandler<CreateDimensionPacket, IMessage> {

    public CreateDimensionHandler() {}

    @Override
    public IMessage onMessage(CreateDimensionPacket message, MessageContext ctx) {
        PocketManager.getDimwatcher().onCreated(message.getDimensionData());

        return null;
    }
}
