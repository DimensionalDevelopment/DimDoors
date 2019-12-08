package org.dimdev.dimdoors.rift.targets;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.limbo.LimboDimension;
import org.dimdev.pocketlib.VirtualLocation;
import org.dimdev.util.Location;
import org.dimdev.util.TeleportUtil;

import java.util.UUID;

public class EscapeTarget extends VirtualTarget implements EntityTarget { // TODO: createRift option
    protected boolean canEscapeLimbo = false;

    public EscapeTarget(boolean canEscapeLimbo) {
        this.canEscapeLimbo = canEscapeLimbo;
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
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        if (!ModDimensions.isDimDoorsPocketDimension(entity.world) && !(entity.world.dimension instanceof LimboDimension)) {
            entity.sendMessage(new TranslatableText("rifts.destinations.escape.not_in_pocket_dim"));
            return false;
        }
        if (entity.world.dimension instanceof LimboDimension && !canEscapeLimbo) {
            entity.sendMessage(new TranslatableText("rifts.destinations.escape.cannot_escape_limbo"));
            return false;
        }

        UUID uuid = entity.getUuid();
        if (uuid != null) {
            Location destLoc = RiftRegistry.instance().getOverworldRift(uuid);
            if (destLoc != null && destLoc.getBlockEntity() instanceof RiftBlockEntity || canEscapeLimbo) {
                TeleportUtil.teleport(entity, VirtualLocation.fromLocation(new Location(entity.world, entity.getBlockPos())).projectToWorld(false));
                return true;
            } else {
                if (destLoc == null) {
                    entity.sendMessage(new TranslatableText("rifts.destinations.escape.did_not_use_rift"));
                } else {
                    entity.sendMessage(new TranslatableText("rifts.destinations.escape.rift_has_closed"));
                }
                TeleportUtil.teleport(entity, LimboDimension.getLimboSkySpawn(entity));
                return true;
            }
        } else {
            return false; // No escape info for that entity
        }
    }
}
