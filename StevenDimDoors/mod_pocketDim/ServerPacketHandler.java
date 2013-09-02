package StevenDimDoors.mod_pocketDim;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.IOpaqueMessage;
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
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		
	}
	
	private class DimWatcher implements IUpdateWatcher
	{
		@Override
		public void onCreated(IOpaqueMessage message)
		{
			sendMessageToAllPlayers(PacketConstants.CREATE_DIM_PACKET_ID, message);
		}

		@Override
		public void onUpdated(IOpaqueMessage message)
		{
			sendMessageToAllPlayers(PacketConstants.UPDATE_DIM_PACKET_ID, message);
		}

		@Override
		public void onDeleted(IOpaqueMessage message)
		{
			sendMessageToAllPlayers(PacketConstants.DELETE_DIM_PACKET_ID, message);
		}	
	}
	
	private class LinkWatcher implements IUpdateWatcher
	{
		@Override
		public void onCreated(IOpaqueMessage message)
		{
			sendMessageToAllPlayers(PacketConstants.CREATE_LINK_PACKET_ID, message);
		}

		@Override
		public void onUpdated(IOpaqueMessage message)
		{
			sendMessageToAllPlayers(PacketConstants.UPDATE_LINK_PACKET_ID, message);
		}

		@Override
		public void onDeleted(IOpaqueMessage message)
		{
			sendMessageToAllPlayers(PacketConstants.DELETE_LINK_PACKET_ID, message);
		}
	}
	
	private static void sendMessageToAllPlayers(byte id, IOpaqueMessage message)
	{
		try
		{
			Packet250CustomPayload packet = new Packet250CustomPayload();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(buffer);
			writer.writeByte(id);
			message.writeToStream(writer);
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
