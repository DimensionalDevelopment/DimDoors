package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.Pocket;
import com.zixiken.dimdoors.shared.PocketRegistry;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.TeleportHelper;
import net.minecraft.entity.Entity;

public class TileEntityDimDoorWarp extends TileEntityDimDoor {

    @Override
    public boolean tryTeleport(Entity entity) {
        if (isPaired()) {
            return RiftRegistry.Instance.teleportEntityToRift(entity, getPairedRiftID());
        }
        if (!(this.isInPocket)) {
            return false;
        } else {
            Pocket pocket = PocketRegistry.Instance.getPocket(this.pocketID, this.getPocketType());
            return TeleportHelper.teleport(entity, pocket.getDepthZeroLocation());
        }
    }
}
