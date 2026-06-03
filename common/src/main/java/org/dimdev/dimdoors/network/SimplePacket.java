package org.dimdev.dimdoors.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

import java.io.IOException;

public interface SimplePacket<T> {
    SimplePacket<T> read(FriendlyByteBuf buf) throws IOException;

    FriendlyByteBuf write(FriendlyByteBuf buf) throws IOException;

    void apply(T listener);

    Identifier channelId();
}
