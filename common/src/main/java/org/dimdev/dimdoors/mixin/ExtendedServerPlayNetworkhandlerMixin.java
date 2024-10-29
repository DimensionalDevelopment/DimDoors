package org.dimdev.dimdoors.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerGamePacketListenerImpl.class)
public class ExtendedServerPlayNetworkhandlerMixin implements ExtendedServerPlayNetworkHandler {

	private final ServerPacketHandler dimdoorsServerPacketHandler = new ServerPacketHandler((ServerGamePacketListenerImpl) (Object) this);

	@Override
	public ServerPacketHandler getDimDoorsPacketHandler() {
		return dimdoorsServerPacketHandler;
	}

}
