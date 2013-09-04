package StevenDimDoors.mod_pocketDim;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.watcher.ClientDimData;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateWatcher;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ServerPacketHandler implements IPacketHandler
{
	public ServerPacketHandler()
	{
		PocketManager.registerDimWatcher(new DimWatcher());
		PocketManager.registerLinkWatcher(new LinkWatcher());
	}
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) { }
	
	private static class DimWatcher implements IUpdateWatcher<ClientDimData>
	{
		@Override
		public void onCreated(ClientDimData message)
		{
			sendDimPacket(PacketConstants.CREATE_DIM_PACKET_ID, message);
		}

		@Override
		public void onDeleted(ClientDimData message)
		{
			sendDimPacket(PacketConstants.DELETE_DIM_PACKET_ID, message);
		}	
	}
	
	private static class LinkWatcher implements IUpdateWatcher<Point4D>
	{
		@Override
		public void onCreated(Point4D message)
		{
			sendLinkPacket(PacketConstants.CREATE_LINK_PACKET_ID, message);
		}

		@Override
		public void onDeleted(Point4D message)
		{
			sendLinkPacket(PacketConstants.DELETE_LINK_PACKET_ID, message);
		}
	}
	
	private static void sendDimPacket(byte id, ClientDimData data)
	{
		try
		{
			Packet250CustomPayload packet = new Packet250CustomPayload();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(buffer);
			writer.writeByte(id);
			data.write(writer);
			writer.close();
			packet.channel = PacketConstants.CHANNEL_NAME;
			packet.data = buffer.toByteArray();
			packet.length = packet.data.length;
			PacketDispatcher.sendPacketToAllPlayers(packet);
		}
		catch (IOException e)
		{
			//This shouldn't happen...
			e.printStackTrace();
		}
	}
	
	private static void sendLinkPacket(byte id, Point4D data)
	{
		try
		{
			Packet250CustomPayload packet = new Packet250CustomPayload();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(buffer);
			writer.writeByte(id);
			Point4D.write(data, writer);
			writer.close();
			packet.channel = PacketConstants.CHANNEL_NAME;
			packet.data = buffer.toByteArray();
			packet.length = packet.data.length;
			PacketDispatcher.sendPacketToAllPlayers(packet);
		}
		catch (IOException e)
		{
			//This shouldn't happen...
			e.printStackTrace();
		}
	}
}
