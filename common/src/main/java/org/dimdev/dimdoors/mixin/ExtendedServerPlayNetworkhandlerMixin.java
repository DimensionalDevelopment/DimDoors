package org.dimdev.dimdoors.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerGamePacketListenerImpl.class)
public class ExtendedServerPlayNetworkhandlerMixin implements ExtendedServerPlayNetworkHandler {
	@Final @Shadow
	private MinecraftServer server;
	private final ServerPacketHandler dimdoorsServerPacketHandler = new ServerPacketHandler((ServerGamePacketListenerImpl) (Object) this);

	@Override
	public ServerPacketHandler getDimDoorsPacketHandler() {
		return dimdoorsServerPacketHandler;
	}

	@Override
	public MinecraftServer dimdoorsGetServer() {
		return server;
	}
}
