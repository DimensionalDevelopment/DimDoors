package org.dimdev.dimdoors.shared.rifts;

import org.dimdev.dimdoors.shared.VirtualLocation;
import org.dimdev.dimdoors.shared.pockets.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class NewPublicDestination extends RiftDestination { // TODO: more config options such as non-default size, etc.
    //public NewPublicDestination() {}

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public boolean teleport(TileEntityRift rift, Entity entity) {
        VirtualLocation newVirtualLocation = null;
        if (rift.virtualLocation != null) {
            int depth = rift.virtualLocation.getDepth();
            if (depth == 0) depth++;
            newVirtualLocation = new VirtualLocation(rift.virtualLocation.getLocation(), depth);
        }
        Pocket pocket = PocketGenerator.generatePublicPocket(newVirtualLocation);
        pocket.setup();
        pocket.linkPocketTo(new GlobalDestination(rift.getLocation()));
        rift.makeDestinationPermanent(weightedDestination, pocket.getEntrance());
        ((TileEntityRift) pocket.getEntrance().getTileEntity()).teleportTo(entity);
        return true;
    }
}
