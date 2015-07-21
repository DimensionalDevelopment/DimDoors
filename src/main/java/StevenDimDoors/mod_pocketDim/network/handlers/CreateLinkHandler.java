package StevenDimDoors.mod_pocketDim.network.handlers;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.network.packets.CreateLinkPacket;
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
