package org.dimdev.dimdoors.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import net.fabricmc.api.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.network.client.ExtendedClientPlayNetworkHandler;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientPacketListener.class)
public class ExtendedClientPlayNetworkHandlerMixin implements ExtendedClientPlayNetworkHandler {
	@Final @Shadow
	private Minecraft minecraft;
	private final ClientPacketHandler dimdoors_PacketHandler = new ClientPacketHandler((ClientPacketListener) (Object) this);

	@Unique
	public ClientPacketHandler getDimDoorsPacketHandler() {
		return dimdoors_PacketHandler;
	}

	@Unique
	public Minecraft dimdoorsGetClient() {
		return minecraft;
	}
}
