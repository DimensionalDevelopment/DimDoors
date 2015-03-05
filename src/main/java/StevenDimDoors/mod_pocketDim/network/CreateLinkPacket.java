package StevenDimDoors.mod_pocketDim.network;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.io.IOException;

public class CreateLinkPacket extends DimDoorsPacket {

    private ClientLinkData clientLinkData = null;

    public CreateLinkPacket() {
    }

    public CreateLinkPacket(ClientLinkData data) {
        this.clientLinkData = data;
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        if (clientLinkData != null) {
            try {
                clientLinkData.write(out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void read(ByteArrayDataInput in) {
        try {
            clientLinkData = ClientLinkData.read(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClient(World world, EntityPlayer player) {
        PocketManager.getLinkWatcher().onCreated(clientLinkData);
    }

    @Override
    public void handleServer(World world, EntityPlayerMP player) {
        //Shouldn't be here
    }
}
