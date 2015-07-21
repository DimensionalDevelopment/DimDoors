package StevenDimDoors.mod_pocketDim.network.packets;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.ClientDimData;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.io.IOException;

public class CreateDimensionPacket implements IMessage {
    private ClientDimData dimensionData = null;

    public CreateDimensionPacket() {}
    public CreateDimensionPacket(ClientDimData data) {
        this.dimensionData = data;
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
