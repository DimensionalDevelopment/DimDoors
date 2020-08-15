package org.dimdev.dimdoors.rift.targets;

import java.util.UUID;

import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.PocketRegistry;
import org.dimdev.dimdoors.world.pocket.PrivatePocketData;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.util.Location;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;

//import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;

public class PrivatePocketExitTarget extends VirtualTarget implements EntityTarget {
    public PrivatePocketExitTarget() {
    }

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
    public boolean receiveEntity(Entity entity, float yawOffset) {
        Location destLoc;
        // TODO: make this recursive
        UUID uuid = EntityUtils.getOwner(entity).getUuid();
        if (uuid != null) {
            destLoc = RiftRegistry.instance(entity.world).getPrivatePocketExit(uuid);
            Pocket pocket = PrivatePocketData.instance(entity.world).getPrivatePocket(uuid);
            if (ModDimensions.isDimDoorsPocketDimension(location.world) && pocket != null && PocketRegistry.instance(pocket.world).getPocketAt(location.pos).equals(pocket)) {
                RiftRegistry.instance(entity.world).setLastPrivatePocketEntrance(uuid, location); // Remember which exit was used for next time the pocket is entered
            }
            if (destLoc == null || !(destLoc.getBlockEntity() instanceof RiftBlockEntity)) {
                if (destLoc == null) {
                    EntityUtils.chat(entity, new TranslatableText("rifts.destinations.private_pocket_exit.did_not_use_rift"));
                } else {
                    EntityUtils.chat(entity, new TranslatableText("rifts.destinations.private_pocket_exit.rift_has_closed"));
                }
                //FabricDimensions.teleport(entity, entity.getServer().getWorld(ModDimensions.LIMBO));
                return false;
            } else {
                ((EntityTarget) destLoc.getBlockEntity()).receiveEntity(entity, yawOffset);
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
        RiftRegistry.instance(location.world).addPocketEntrance(pocket, location);
    }

    @Override
    public float[] getColor() {
        return new float[]{0, 1, 0, 1};
    }
}
