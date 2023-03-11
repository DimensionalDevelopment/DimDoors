package org.dimdev.dimdoors.network;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface SimplePacket<T> {
	SimplePacket<T> read(FriendlyByteBuf buf) throws IOException;

	FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException;

	void apply(T listener);

	ResourceLocation channelId();
}
