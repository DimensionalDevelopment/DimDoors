package StevenDimDoors.mod_pocketDim.network.packets;

import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

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
