package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.Pocket;
import com.zixiken.dimdoors.shared.PocketRegistry;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.TeleportHelper;
import com.zixiken.dimdoors.shared.util.Location;
import java.util.Random;
import net.minecraft.entity.Entity;

public class TileEntityTransTrapdoor extends DDTileEntityBase {

    @Override
    public float[] getRenderColor(Random rand) {
        float[] rgbaColor = {1, 1, 1, 1};
        if (this.world.provider.getDimension() == -1) {
            rgbaColor[0] = world.rand.nextFloat() * 0.5F + 0.4F;
            rgbaColor[1] = world.rand.nextFloat() * 0.05F;
            rgbaColor[2] = world.rand.nextFloat() * 0.05F;
        } else {
            rgbaColor[0] = world.rand.nextFloat() * 0.5F + 0.1F;
            rgbaColor[1] = world.rand.nextFloat() * 0.4F + 0.4F;
            rgbaColor[2] = world.rand.nextFloat() * 0.6F + 0.5F;
        }
        return rgbaColor;
    }
    
    @Override
    public Location getTeleportTargetLocation() {
        return new Location(this.getWorld().provider.getDimension(), this.getPos().up());
    }

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
            Pocket pocket = PocketRegistry.INSTANCE.getPocket(this.pocketID, this.getPocketType());
            teleportLocation = pocket.getDepthZeroLocation();
        }
        return TeleportHelper.teleport(entity, teleportLocation);
    }
}
