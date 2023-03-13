package org.dimdev.dimdoors.network.client;

import net.fabricmc.api.Dist;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(Dist.CLIENT)
public interface ExtendedClientPlayNetworkHandler {
	ClientPacketHandler getDimDoorsPacketHandler();

	Minecraft dimdoorsGetClient();
}
