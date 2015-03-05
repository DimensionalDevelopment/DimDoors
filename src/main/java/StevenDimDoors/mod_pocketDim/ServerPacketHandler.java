package StevenDimDoors.mod_pocketDim;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import StevenDimDoors.mod_pocketDim.network.*;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.watcher.ClientDimData;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateWatcher;
import net.minecraft.network.Packet;

public class ServerPacketHandler
{
	public ServerPacketHandler()
	{
		PocketManager.registerDimWatcher(new DimWatcher());
		PocketManager.registerLinkWatcher(new LinkWatcher());
	}
	
	private static class DimWatcher implements IUpdateWatcher<ClientDimData>
	{
		@Override
		public void onCreated(ClientDimData message)
		{
            DimDoorsNetwork.sendToAllPlayers(new CreateDimensionPacket(message));
		}

		@Override
		public void onDeleted(ClientDimData message)
		{
            DimDoorsNetwork.sendToAllPlayers(new DeleteDimensionPacket(message));
		}

		@Override
		public void update(ClientDimData message)
		{
			// TODO Auto-generated method stub
			
		}	
	}
	
	private static class LinkWatcher implements IUpdateWatcher<ClientLinkData>
	{
		@Override
		public void onCreated(ClientLinkData message)
		{
			DimDoorsNetwork.sendToAllPlayers(new CreateLinkPacket(message));
		}

		@Override
		public void onDeleted(ClientLinkData message)
		{
            DimDoorsNetwork.sendToAllPlayers(new DeleteLinkPacket(message));
		}

		@Override
		public void update(ClientLinkData message)
		{
            DimDoorsNetwork.sendToAllPlayers(new UpdateLinkPacket(message));
		}
	}
}
