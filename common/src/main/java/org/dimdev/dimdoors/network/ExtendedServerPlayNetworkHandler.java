package org.dimdev.dimdoors.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public interface ExtendedServerPlayNetworkHandler {
	static ExtendedServerPlayNetworkHandler get(ServerPlayNetworkHandler networkHandler) {
		return (ExtendedServerPlayNetworkHandler) networkHandler;
	}

	ServerPacketHandler getDimDoorsPacketHandler();

	MinecraftServer dimdoorsGetServer();
}
