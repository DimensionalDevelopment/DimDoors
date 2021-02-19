package org.dimdev.dimdoors.network;

import net.minecraft.server.MinecraftServer;

public interface ExtendedServerPlayNetworkHandler {
	ServerPacketHandler getDimDoorsPacketHandler();

	MinecraftServer dimdoorsGetServer();
}
