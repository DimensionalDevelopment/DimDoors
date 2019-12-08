package org.dimdev.dimdoors.rift.targets;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.limbo.LimboDimension;
import org.dimdev.dimdoors.world.pocketdimension.PersonalPocketDimension;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;
import org.dimdev.pocketlib.PrivatePocketData;
import org.dimdev.util.EntityUtils;
import org.dimdev.util.Location;
import org.dimdev.util.TeleportUtil;

import java.util.UUID;

public class PrivatePocketExitTarget extends VirtualTarget implements EntityTarget {
    public PrivatePocketExitTarget() {}

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        return nbt;
    }

    @Override
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        Location destLoc;
        // TODO: make this recursive
        UUID uuid = EntityUtils.getOwner(entity).getUuid();
        if (uuid != null) {
            destLoc = RiftRegistry.instance().getPrivatePocketExit(uuid);
            Pocket pocket = PrivatePocketData.instance().getPrivatePocket(uuid);
            if (location.world.dimension instanceof PersonalPocketDimension && pocket != null && PocketRegistry.instance(pocket.world).getPocketAt(location.pos).equals(pocket)) {
                RiftRegistry.instance().setLastPrivatePocketEntrance(uuid, location); // Remember which exit was used for next time the pocket is entered
            }
            if (destLoc == null || !(destLoc.getBlockEntity() instanceof RiftBlockEntity)) {
                if (destLoc == null) {
                    DimDoors.sendTranslatedMessage(entity, "rifts.destinations.private_pocket_exit.did_not_use_rift");
                } else {
                    DimDoors.sendTranslatedMessage(entity, "rifts.destinations.private_pocket_exit.rift_has_closed");
                }
                TeleportUtil.teleport(entity, LimboDimension.getLimboSkySpawn(entity));
                return false;
            } else {
                ((EntityTarget) destLoc.getBlockEntity()).receiveEntity(entity, relativeYaw, relativePitch);
                return true;
            }
        } else {
            return false; // Non-player/owned entity tried to escape/leave private pocket
        }
    }

    @Override
    public void register() {
        super.register();
        PocketRegistry privatePocketRegistry = PocketRegistry.instance(location.world);
        Pocket pocket = privatePocketRegistry.getPocketAt(location.pos);
        RiftRegistry.instance().addPocketEntrance(pocket, location);
    }

    @Override
    public float[] getColor() {
        return new float[]{0, 1, 0, 1};
    }
}
