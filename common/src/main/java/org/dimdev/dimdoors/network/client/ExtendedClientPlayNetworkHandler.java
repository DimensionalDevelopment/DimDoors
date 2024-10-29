package org.dimdev.dimdoors.network.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ExtendedClientPlayNetworkHandler {
	ClientPacketHandler getDimDoorsPacketHandler();
}
