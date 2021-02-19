package org.dimdev.dimdoors.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public interface ExtendedClientPlayNetworkHandler {
	ClientPacketHandler getDimDoorsPacketHandler();

	MinecraftClient dimdoorsGetClient();
}
