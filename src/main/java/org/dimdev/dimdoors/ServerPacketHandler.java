package org.dimdev.dimdoors;

import org.dimdev.dimdoors.core.PocketManager;
import org.dimdev.dimdoors.network.DimDoorsNetwork;
import org.dimdev.dimdoors.network.packets.*;
import org.dimdev.dimdoors.watcher.ClientDimData;
import org.dimdev.dimdoors.watcher.ClientLinkData;
import org.dimdev.dimdoors.watcher.IUpdateWatcher;

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
        public void update(ClientDimData message) {
            // TODO Auto-generated method stub

        }
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
