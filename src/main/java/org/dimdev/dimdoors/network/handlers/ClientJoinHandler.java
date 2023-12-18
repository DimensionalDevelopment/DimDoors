package org.dimdev.dimdoors.network.handlers;

import org.dimdev.dimdoors.core.NewDimData;
import org.dimdev.dimdoors.core.PocketManager;
import org.dimdev.dimdoors.mod_pocketDim;
import org.dimdev.dimdoors.network.packets.ClientJoinPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;

public class ClientJoinHandler implements IMessageHandler<ClientJoinPacket, IMessage> {

    public ClientJoinHandler() {
    }

    @Override
    public IMessage onMessage(ClientJoinPacket message, MessageContext ctx) {
        EntityPlayer player = mod_pocketDim.proxy.getMessagePlayer(ctx);
        NewDimData dimensionData = PocketManager.getDimensionData(player.worldObj);

        if (dimensionData.isPocketDimension())
            player.worldObj.provider.registerWorld(player.worldObj);

        return null;
    }
}
