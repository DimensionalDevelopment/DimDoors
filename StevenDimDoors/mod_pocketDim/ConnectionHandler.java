package StevenDimDoors.mod_pocketDim;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ConnectionHandler implements IConnectionHandler
{
	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
	{
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) { }
	
	@Override
	public void connectionOpened(NetHandler netClientHandler,MinecraftServer server, INetworkManager manager) { }

	@Override
	public void connectionClosed(INetworkManager manager) { }

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) { }

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
	{
		//Send information about all the registered dimensions and links to the client
		try
		{
			Packet250CustomPayload packet = new Packet250CustomPayload();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(buffer);
			writer.writeByte(PacketConstants.CLIENT_JOIN_PACKET_ID);
			PocketManager.writePacket(writer);
			writer.close();
			packet.channel = PacketConstants.CHANNEL_NAME;
			packet.data = buffer.toByteArray();
			packet.length = packet.data.length;
			manager.addToSendQueue(packet);
		}
		catch (IOException e)
		{
			//This shouldn't happen...
			e.printStackTrace();
		}
	}
}