package org.dimdev.dimdoors.rift.targets;

import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.VirtualLocation;
import org.dimdev.util.Location;

public class PublicPocketTarget extends RestoringTarget {
    public PublicPocketTarget() {}

    @Override
    public void fromTag(CompoundTag nbt) { super.fromTag(nbt); }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        return nbt;
    }

    @Override
    public Location makeLinkTarget() {
        VirtualLocation riftVirtualLocation = VirtualLocation.fromLocation(location);
        VirtualLocation newVirtualLocation = null;
        if (riftVirtualLocation != null) {
            int depth = Math.max(riftVirtualLocation.depth, 1);
            newVirtualLocation = new VirtualLocation(riftVirtualLocation.world, riftVirtualLocation.x, riftVirtualLocation.z, depth);
        }
        Pocket pocket = PocketGenerator.generatePublicPocket(newVirtualLocation, new GlobalReference(location), null);

        return RiftRegistry.instance(location.world).getPocketEntrance(pocket);
    }
}
