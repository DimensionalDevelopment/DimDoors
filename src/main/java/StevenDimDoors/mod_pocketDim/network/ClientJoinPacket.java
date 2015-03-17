package StevenDimDoors.mod_pocketDim.network;

import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.io.IOException;

public class ClientJoinPacket extends DimDoorsPacket {
    @Override
    public void write(ByteArrayDataOutput out) {
        try {
            PocketManager.writePacket(out);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void read(ByteArrayDataInput in) {
        try {
            PocketManager.readPacket(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClient(World world, EntityPlayer player) {
        NewDimData dimensionData = PocketManager.getDimensionData(player.worldObj);

        if (dimensionData.isPocketDimension())
            player.worldObj.provider.registerWorld(player.worldObj);
    }

    @Override
    public void handleServer(World world, EntityPlayerMP player) {

    }
}
