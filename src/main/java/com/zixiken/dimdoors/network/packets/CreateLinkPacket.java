package com.zixiken.dimdoors.network.packets;

import com.zixiken.dimdoors.watcher.ClientLinkData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class CreateLinkPacket implements IMessage {

    private ClientLinkData clientLinkData = null;

    public CreateLinkPacket() {
    }

    public CreateLinkPacket(ClientLinkData data) {
        this.clientLinkData = data;
    }

    public ClientLinkData getClientLinkData() { return clientLinkData; }

    @Override
    public void fromBytes(ByteBuf in) {
        try {
            clientLinkData = ClientLinkData.read(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf out) {
        if (clientLinkData != null) {
            try {
                clientLinkData.write(out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
