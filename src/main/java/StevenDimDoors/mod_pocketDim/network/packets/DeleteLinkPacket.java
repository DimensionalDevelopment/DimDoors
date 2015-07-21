package StevenDimDoors.mod_pocketDim.network.packets;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.io.IOException;

public class DeleteLinkPacket implements IMessage {
    private ClientLinkData linkData;

    public DeleteLinkPacket() {}
    public DeleteLinkPacket(ClientLinkData linkData) {
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
