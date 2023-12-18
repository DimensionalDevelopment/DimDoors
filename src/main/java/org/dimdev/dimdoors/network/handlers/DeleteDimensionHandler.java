package org.dimdev.dimdoors.network.handlers;

import org.dimdev.dimdoors.core.PocketManager;
import org.dimdev.dimdoors.network.packets.DeleteDimensionPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DeleteDimensionHandler implements IMessageHandler<DeleteDimensionPacket, IMessage> {

    public DeleteDimensionHandler() {
    }

    @Override
    public IMessage onMessage(DeleteDimensionPacket message, MessageContext ctx) {
        PocketManager.getDimwatcher().onDeleted(message.getDimensionData());

        return null;
    }
}
