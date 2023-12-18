package org.dimdev.dimdoors.network.handlers;

import org.dimdev.dimdoors.core.PocketManager;
import org.dimdev.dimdoors.network.packets.CreateDimensionPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class CreateDimensionHandler implements IMessageHandler<CreateDimensionPacket, IMessage> {

    public CreateDimensionHandler() {
    }

    @Override
    public IMessage onMessage(CreateDimensionPacket message, MessageContext ctx) {
        PocketManager.getDimwatcher().onCreated(message.getDimensionData());

        return null;
    }
}
