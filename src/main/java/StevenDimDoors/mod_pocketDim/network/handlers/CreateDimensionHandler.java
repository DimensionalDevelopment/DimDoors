package StevenDimDoors.mod_pocketDim.network.handlers;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.network.packets.CreateDimensionPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class CreateDimensionHandler implements IMessageHandler<CreateDimensionPacket, IMessage> {

    public CreateDimensionHandler() {}

    @Override
    public IMessage onMessage(CreateDimensionPacket message, MessageContext ctx) {
        PocketManager.getDimwatcher().onCreated(message.getDimensionData());

        return null;
    }
}
