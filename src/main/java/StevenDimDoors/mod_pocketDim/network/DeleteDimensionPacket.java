package StevenDimDoors.mod_pocketDim.network;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.ClientDimData;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.io.IOException;

public class DeleteDimensionPacket extends DimDoorsPacket {
    private ClientDimData dimensionData = null;

    public DeleteDimensionPacket() {}
    public DeleteDimensionPacket(ClientDimData dimensionData) {
        this.dimensionData = dimensionData;
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        if (dimensionData != null) {
            try {
                dimensionData.write(out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void read(ByteArrayDataInput in) {
        try {
            dimensionData = ClientDimData.read(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClient(World world, EntityPlayer player) {
        PocketManager.getDimwatcher().onDeleted(dimensionData);
    }

    @Override
    public void handleServer(World world, EntityPlayerMP player) {
        //Shouldn't be here
    }
}
