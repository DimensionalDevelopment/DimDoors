package StevenDimDoors.mod_pocketDimClient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import StevenDimDoors.mod_pocketDim.PacketConstants;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.watcher.ClientDimData;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateSource;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateWatcher;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler implements IPacketHandler, IUpdateSource
{
	private IUpdateWatcher<Point4D> linkWatcher;
	private IUpdateWatcher<ClientDimData> dimWatcher;
	
	public ClientPacketHandler()
	{
		PocketManager.getWatchers(this);
	}

	@Override
	public void registerWatchers(IUpdateWatcher<ClientDimData> dimWatcher, IUpdateWatcher<Point4D> linkWatcher)
	{
		this.dimWatcher = dimWatcher;
		this.linkWatcher = linkWatcher;
	}
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		// TODO: Is this even necessary? I'm not convinced we can receive packets from other channels anyway!
		if (!packet.channel.equals(PacketConstants.CHANNEL_NAME))
			return;
		
		// If this is a memory connection, then our client is running an integrated server.
		// We can tell by checking if packet size is 0.
		if (manager.packetSize() == 0)
			return;
		
		try
		{
			DataInputStream input = new DataInputStream(new ByteArrayInputStream(packet.data));
			byte packetID = input.readByte();
			switch (packetID)
			{
				case PacketConstants.CLIENT_JOIN_PACKET_ID:
					PocketManager.readPacket(input);
					break;
				case PacketConstants.CREATE_DIM_PACKET_ID:
					dimWatcher.onCreated( ClientDimData.read(input) );
					break;
				case PacketConstants.CREATE_LINK_PACKET_ID:
					linkWatcher.onCreated( Point4D.read(input) );
					break;
				case PacketConstants.DELETE_DIM_PACKET_ID:
					dimWatcher.onDeleted( ClientDimData.read(input) );
					break;
				case PacketConstants.DELETE_LINK_PACKET_ID:
					linkWatcher.onDeleted( Point4D.read(input) );
					break;
			}
		}
		catch (Exception e)
		{
			System.err.println("An exception occurred while processing a data packet:");
			e.printStackTrace();
		}
	}
}
