package com.zixiken.dimdoors.network.packets;

import com.zixiken.dimdoors.watcher.ClientLinkData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class UpdateLinkPacket implements IMessage {
    private ClientLinkData linkData = null;

    public UpdateLinkPacket() {}
    public UpdateLinkPacket(ClientLinkData linkData) {
        this.linkData = linkData;
    }

    public ClientLinkData getLinkData() { return linkData; }

    @Override
    public void fromBytes(ByteBuf in) {
        try {
            linkData = ClientLinkData.read(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf out) {
        if (linkData != null) {
            try {
                linkData.write(out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
