package com.zixiken.dimdoors.network.handlers;

import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.network.packets.DeleteDimensionPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DeleteDimensionHandler implements IMessageHandler<DeleteDimensionPacket, IMessage> {

    public DeleteDimensionHandler() {}

    @Override
    public IMessage onMessage(DeleteDimensionPacket message, MessageContext ctx) {
        PocketManager.getDimwatcher().onDeleted(message.getDimensionData());

        return null;
    }
}
