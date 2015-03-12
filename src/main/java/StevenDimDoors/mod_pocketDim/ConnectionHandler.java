package StevenDimDoors.mod_pocketDim;

import StevenDimDoors.mod_pocketDim.network.ClientJoinPacket;
import StevenDimDoors.mod_pocketDim.network.DimDoorsNetwork;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.ClientDimData;
import net.minecraftforge.common.network.ForgeMessage;
import net.minecraftforge.common.network.ForgeNetworkHandler;

public class ConnectionHandler
{
    @SubscribeEvent
    public void serverConnectionFromClientEvent(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
            NetHandlerPlayServer server = ((NetHandlerPlayServer)event.handler);
            FMLEmbeddedChannel channel =  NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
            for (NewDimData data : PocketManager.getDimensions()) {
                try {
                    if (data.isPocketDimension() || data.id() == mod_pocketDim.properties.LimboDimensionID) {
                        channel.writeOutbound(new ForgeMessage.DimensionRegisterMessage(data.id(), DimensionManager.getProviderType(data.id())));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

	@SubscribeEvent
	public void connectionClosed(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
	{
        PocketManager.tryUnload();
	}

	@SubscribeEvent
	public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		// Hax... please don't do this! >_<
        DimDoorsNetwork.sendToPlayer(new ClientJoinPacket(), event.player);
		
	}
}