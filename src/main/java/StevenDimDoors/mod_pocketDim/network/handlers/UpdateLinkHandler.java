package StevenDimDoors.mod_pocketDim.network.handlers;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.network.packets.UpdateLinkPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UpdateLinkHandler implements IMessageHandler<UpdateLinkPacket, IMessage> {
    @Override
    public IMessage onMessage(UpdateLinkPacket message, MessageContext ctx) {
        PocketManager.getLinkWatcher().update(message.getLinkData());

        return null;
    }
}
