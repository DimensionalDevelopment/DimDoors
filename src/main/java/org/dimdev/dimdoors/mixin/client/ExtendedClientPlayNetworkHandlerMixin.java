package org.dimdev.dimdoors.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.network.client.ExtendedClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ExtendedClientPlayNetworkHandlerMixin implements ExtendedClientPlayNetworkHandler {
	@Final @Shadow
	private MinecraftClient client;
	private final ClientPacketHandler dimdoors_PacketHandler = new ClientPacketHandler((ClientPlayNetworkHandler) (Object) this);

	@Unique
	public ClientPacketHandler getDimDoorsPacketHandler() {
		return dimdoors_PacketHandler;
	}

	@Unique
	public MinecraftClient dimdoorsGetClient() {
		return client;
	}
}
