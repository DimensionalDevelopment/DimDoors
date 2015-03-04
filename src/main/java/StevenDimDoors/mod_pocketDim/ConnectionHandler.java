package StevenDimDoors.mod_pocketDim;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.ClientDimData;

public class ConnectionHandler implements IConnectionHandler
{
	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
	{
		for(NewDimData data : PocketManager.getDimensions())
		{
			try
			{
				if(data.isPocketDimension()||data.id()==mod_pocketDim.properties.LimboDimensionID)
				{
					Packet250CustomPayload[] pkt = ForgePacket.makePacketSet(new DimensionRegisterPacket(data.id(), DimensionManager.getProviderType(data.id())));
					manager.addToSendQueue(pkt[0]);
				}
			}
			catch(Exception E)
			{
				E.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) { }
	
	@Override
	public void connectionOpened(NetHandler netClientHandler,MinecraftServer server, INetworkManager manager) { }

	@Override
	public void connectionClosed(INetworkManager manager) 
	{
		if(PocketManager.isConnected)
		{
			PocketManager.unload();
		}
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) { }

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
	{
		// Hax... please don't do this! >_< 
		PocketManager.getDimwatcher().onCreated(new ClientDimData(PocketManager.createDimensionDataDangerously(0)));
		
	}
}