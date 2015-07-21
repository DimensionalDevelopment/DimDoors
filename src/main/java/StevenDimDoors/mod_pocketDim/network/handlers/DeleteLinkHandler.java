package StevenDimDoors.mod_pocketDim.network.handlers;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.network.packets.DeleteLinkPacket;
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
