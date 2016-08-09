package com.zixiken.dimdoors.watcher;

import com.zixiken.dimdoors.network.packets.*;
import com.zixiken.dimdoors.watcher.ClientLinkData;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.watcher.ClientDimData;
import com.zixiken.dimdoors.watcher.IUpdateWatcher;
import com.zixiken.dimdoors.network.DimDoorsNetwork;

public class ServerPacketHandler {
	public ServerPacketHandler() {
		PocketManager.registerDimWatcher(new DimWatcher());
		PocketManager.registerLinkWatcher(new LinkWatcher());
	}
	
	private static class DimWatcher implements IUpdateWatcher<ClientDimData> {
		@Override
		public void onCreated(ClientDimData message) {
            DimDoorsNetwork.sendToAllPlayers(new CreateDimensionPacket(message));
		}

		@Override
		public void onDeleted(ClientDimData message) {
            DimDoorsNetwork.sendToAllPlayers(new DeleteDimensionPacket(message));
		}

		@Override
		public void update(ClientDimData message) {}
	}
	
	private static class LinkWatcher implements IUpdateWatcher<ClientLinkData> {
		@Override
		public void onCreated(ClientLinkData message) {
			DimDoorsNetwork.sendToAllPlayers(new CreateLinkPacket(message));
		}

		@Override
		public void onDeleted(ClientLinkData message) {
            DimDoorsNetwork.sendToAllPlayers(new DeleteLinkPacket(message));
		}

		@Override
		public void update(ClientLinkData message) {
            DimDoorsNetwork.sendToAllPlayers(new UpdateLinkPacket(message));
		}
	}
}
