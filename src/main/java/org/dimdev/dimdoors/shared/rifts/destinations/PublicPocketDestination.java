package org.dimdev.dimdoors.shared.rifts.destinations;

import lombok.*;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.dimdoors.shared.VirtualLocation;
import org.dimdev.dimdoors.shared.pockets.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketGenerator;

@Getter @AllArgsConstructor @NoArgsConstructor @Builder(toBuilder = true) @ToString
public class PublicPocketDestination extends LinkingDestination {

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return nbt; }

    @Override
    public Location makeLinkTarget(RotatedLocation loc, Entity entity) {
        VirtualLocation riftVirtualLocation = VirtualLocation.fromLocation(loc.getLocation());
        VirtualLocation newVirtualLocation = null;
        if (riftVirtualLocation != null) {
            int depth = Math.min(riftVirtualLocation.getDepth(), 1);
            newVirtualLocation = riftVirtualLocation.toBuilder().depth(depth).build();
        }
        Pocket pocket = PocketGenerator.generatePublicPocket(newVirtualLocation);
        pocket.setup();

        pocket.linkPocketTo(new GlobalDestination(loc.getLocation()), null);

        return pocket.getEntrance();
    }
}
