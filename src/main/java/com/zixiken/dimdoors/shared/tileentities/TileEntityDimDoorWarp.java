package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.Pocket;
import com.zixiken.dimdoors.shared.PocketRegistry;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.TeleportHelper;
import com.zixiken.dimdoors.shared.util.Location;
import net.minecraft.entity.Entity;

public class TileEntityDimDoorWarp extends TileEntityDimDoor {

    @Override
    public boolean tryTeleport(Entity entity) {
        Location teleportLocation;
        if (isPaired()) {
            int otherRiftID = getPairedRiftID();
            teleportLocation = RiftRegistry.Instance.getTeleportLocation(otherRiftID);
            RiftRegistry.Instance.validatePlayerPocketEntry(entity, otherRiftID);
        } else if (!(this.isInPocket)) {
            return false;
        } else {
            Pocket pocket = PocketRegistry.Instance.getPocket(this.pocketID, this.getPocketType());
            teleportLocation = pocket.getDepthZeroLocation();
        }
        return TeleportHelper.teleport(entity, teleportLocation);
    }
}
