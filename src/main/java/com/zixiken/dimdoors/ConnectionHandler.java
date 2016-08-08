package com.zixiken.dimdoors;

import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.network.packets.ClientJoinPacket;
import com.zixiken.dimdoors.network.DimDoorsNetwork;
import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.DimensionManager;
import com.zixiken.dimdoors.core.DimData;
import net.minecraftforge.common.network.ForgeMessage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ConnectionHandler {
    @SubscribeEvent
    public void serverConnectionFromClientEvent(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
            NetHandlerPlayServer server = ((NetHandlerPlayServer)event.handler);
            FMLEmbeddedChannel channel =  NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
            for (DimData data : PocketManager.getDimensions()) {
                try {
                    int id = data.id();
                    if (data.isPocketDimension() || id == DDProperties.instance().LimboDimensionID)
                        channel.writeOutbound(new ForgeMessage.DimensionRegisterMessage(id,
                                DimensionManager.getProviderType(id)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

	@SubscribeEvent
	public void connectionClosed(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {PocketManager.tryUnload();}

	@SubscribeEvent
	public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		// Hax... please don't do this! >_<
        DimDoorsNetwork.sendToPlayer(new ClientJoinPacket(), event.player);
	}
}