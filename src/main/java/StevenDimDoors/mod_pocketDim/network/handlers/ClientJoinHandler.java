package StevenDimDoors.mod_pocketDim.network.handlers;

import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.network.packets.ClientJoinPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;

public class ClientJoinHandler implements IMessageHandler<ClientJoinPacket, IMessage> {

    public ClientJoinHandler() {}

    @Override
    public IMessage onMessage(ClientJoinPacket message, MessageContext ctx) {
        EntityPlayer player = mod_pocketDim.proxy.getMessagePlayer(ctx);
        NewDimData dimensionData = PocketManager.getDimensionData(player.worldObj);

        if (dimensionData.isPocketDimension())
            player.worldObj.provider.registerWorld(player.worldObj);

        return null;
    }
}
