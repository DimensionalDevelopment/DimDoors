package com.zixiken.dimdoors.network.handlers;

import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.network.packets.CreateLinkPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CreateLinkHandler implements IMessageHandler<CreateLinkPacket, IMessage> {

    public CreateLinkHandler() {}

    @Override
    public IMessage onMessage(CreateLinkPacket message, MessageContext ctx) {
        PocketManager.getLinkWatcher().onCreated(message.getClientLinkData());

        return null;
    }
}
