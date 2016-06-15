package com.zixiken.dimdoors.network.packets;

import com.zixiken.dimdoors.core.PocketManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class ClientJoinPacket implements IMessage {

    public ClientJoinPacket() {}

    @Override
    public void fromBytes(ByteBuf in) {
        try {
            PocketManager.readPacket(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf out) {
        try {
            PocketManager.writePacket(out);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
