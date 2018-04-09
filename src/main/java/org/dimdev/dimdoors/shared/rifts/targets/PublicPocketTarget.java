package org.dimdev.dimdoors.shared.rifts.targets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.shared.pockets.PocketGenerator;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.VirtualLocation;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class PublicPocketTarget extends RestoringTarget {
    // public PublicPocketDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return nbt; }

    @Override
    public Location makeLinkTarget() {
        VirtualLocation riftVirtualLocation = VirtualLocation.fromLocation(location);
        VirtualLocation newVirtualLocation = null;
        if (riftVirtualLocation != null) {
            int depth = Math.max(riftVirtualLocation.getDepth(), 1);
            newVirtualLocation = riftVirtualLocation.toBuilder().depth(depth).build();
        }
        Pocket pocket = PocketGenerator.generatePublicPocket(newVirtualLocation, new GlobalReference(location), null);

        return RiftRegistry.instance().getPocketEntrance(pocket);
    }
}
