package StevenDimDoors.mod_pocketDim.network.handlers;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.network.packets.DeleteDimensionPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DeleteDimensionHandler implements IMessageHandler<DeleteDimensionPacket, IMessage> {

    public DeleteDimensionHandler() {}

    @Override
    public IMessage onMessage(DeleteDimensionPacket message, MessageContext ctx) {
        PocketManager.getDimwatcher().onDeleted(message.getDimensionData());

        return null;
    }
}
