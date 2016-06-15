package com.zixiken.dimdoors.network.handlers;

import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.network.packets.CreateLinkPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class CreateLinkHandler implements IMessageHandler<CreateLinkPacket, IMessage> {

    public CreateLinkHandler() {}

    @Override
    public IMessage onMessage(CreateLinkPacket message, MessageContext ctx) {
        PocketManager.getLinkWatcher().onCreated(message.getClientLinkData());

        return null;
    }
}
