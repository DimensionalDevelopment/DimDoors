package org.dimdev.dimdoors.network.client;

import net.fabricmc.api.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;

@OnlyIn(Dist.CLIENT)
public interface ExtendedClientPlayNetworkHandler {
	ClientPacketHandler getDimDoorsPacketHandler();

	Minecraft dimdoorsGetClient();
}
