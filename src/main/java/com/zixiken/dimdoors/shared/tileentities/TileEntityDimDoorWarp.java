package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.Pocket;
import com.zixiken.dimdoors.shared.PocketRegistry;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import com.zixiken.dimdoors.shared.util.Location;
import net.minecraft.entity.Entity;

public class TileEntityDimDoorWarp extends TileEntityDimDoor {

    @Override
    public boolean tryTeleport(Entity entity) {
        Location tpLocation;
        if (isPaired()) {
            int otherRiftID = getPairedRiftID();
            tpLocation = RiftRegistry.INSTANCE.getTeleportLocation(otherRiftID);
            RiftRegistry.INSTANCE.validatePlayerPocketEntry(entity, otherRiftID);
        } else if (!(this.isInPocket)) {
            return false;
        } else {
            Pocket pocket = PocketRegistry.INSTANCE.getPocket(this.pocketID, this.getPocketType());
            tpLocation = pocket.getDepthZeroLocation();
        }
        return TeleporterDimDoors.instance().teleport(entity, tpLocation);
    }
}
