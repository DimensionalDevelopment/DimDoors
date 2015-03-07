package StevenDimDoors.mod_pocketDim;

import StevenDimDoors.mod_pocketDim.network.DimDoorsNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.ClientDimData;
import net.minecraftforge.common.network.ForgeMessage;

public class ConnectionHandler
{
	@SubscribeEvent
	public String connectionReceived(FMLNetworkEvent.ServerConnectionFromClientEvent event)
	{
		for(NewDimData data : PocketManager.getDimensions())
		{
			try
			{
				if(data.isPocketDimension()||data.id()==mod_pocketDim.properties.LimboDimensionID)
				{
                    event.manager.channel().writeAndFlush(new ForgeMessage.DimensionRegisterMessage(data.id(), DimensionManager.getProviderType(data.id())));
				}
			}
			catch(Exception E)
			{
				E.printStackTrace();
			}
		}
		return null;
	}

	@SubscribeEvent
	public void connectionClosed(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
	{
		if(PocketManager.isConnected)
		{
			PocketManager.unload();
		}
	}

	@SubscribeEvent
	public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		// Hax... please don't do this! >_< 
		PocketManager.getDimwatcher().onCreated(new ClientDimData(PocketManager.createDimensionDataDangerously(0)));
		
	}
}