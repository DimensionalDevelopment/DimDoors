package com.zixiken.dimdoors.network.packets;

import com.zixiken.dimdoors.watcher.ClientDimData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class DeleteDimensionPacket implements IMessage {
    private ClientDimData dimensionData = null;

    public DeleteDimensionPacket() {}
    public DeleteDimensionPacket(ClientDimData dimensionData) {
        this.dimensionData = dimensionData;
    }

    public ClientDimData getDimensionData() { return dimensionData; }

    @Override
    public void fromBytes(ByteBuf in) {
        try {
            dimensionData = ClientDimData.read(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf out) {
        if (dimensionData != null) {
            try {
                dimensionData.write(out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
