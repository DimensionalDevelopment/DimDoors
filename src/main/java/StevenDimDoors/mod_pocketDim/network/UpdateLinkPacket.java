package StevenDimDoors.mod_pocketDim.network;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.io.IOException;

public class UpdateLinkPacket extends DimDoorsPacket {
    private ClientLinkData linkData = null;

    public UpdateLinkPacket() {}
    public UpdateLinkPacket(ClientLinkData linkData) {
        this.linkData = linkData;
    }
    @Override
    public void write(ByteArrayDataOutput out) {
        if (linkData != null) {
            try {
                linkData.write(out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void read(ByteArrayDataInput in) {
        try {
            linkData = ClientLinkData.read(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClient(World world, EntityPlayer player) {
        PocketManager.getLinkWatcher().update(linkData);
    }

    @Override
    public void handleServer(World world, EntityPlayerMP player) {
        //Shouldn't be here
    }
}
