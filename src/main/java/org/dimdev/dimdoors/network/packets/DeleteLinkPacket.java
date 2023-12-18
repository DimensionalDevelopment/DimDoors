package org.dimdev.dimdoors.network.packets;

import org.dimdev.dimdoors.watcher.ClientLinkData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class DeleteLinkPacket implements IMessage {
    private ClientLinkData linkData;

    public DeleteLinkPacket() {
    }

    public DeleteLinkPacket(ClientLinkData linkData) {
        this.linkData = linkData;
    }

    public ClientLinkData getLinkData() {
        return linkData;
    }

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
