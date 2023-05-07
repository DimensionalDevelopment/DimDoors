package org.dimdev.dimdoors.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public interface ExtendedServerPlayNetworkHandler {
	static ExtendedServerPlayNetworkHandler get(ServerGamePacketListenerImpl networkHandler) {
		return (ExtendedServerPlayNetworkHandler) networkHandler;
	}

	ServerPacketHandler getDimDoorsPacketHandler();

	MinecraftServer dimdoorsGetServer();
}
