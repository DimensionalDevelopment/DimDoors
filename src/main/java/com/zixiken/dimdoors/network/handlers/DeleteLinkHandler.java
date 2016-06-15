package com.zixiken.dimdoors.network.handlers;

import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.network.packets.DeleteLinkPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DeleteLinkHandler implements IMessageHandler<DeleteLinkPacket, IMessage> {
    @Override
    public IMessage onMessage(DeleteLinkPacket message, MessageContext ctx) {
        PocketManager.getLinkWatcher().onDeleted(message.getLinkData());

        return null;
    }
}
