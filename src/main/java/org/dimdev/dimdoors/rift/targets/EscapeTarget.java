package org.dimdev.dimdoors.rift.targets;

import java.util.UUID;

import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.TeleportUtil;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

import static org.dimdev.dimdoors.util.EntityUtils.chat;

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
    public boolean receiveEntity(Entity entity, float yawOffset) {
        if (!ModDimensions.isDimDoorsPocketDimension(entity.world) && !(ModDimensions.isLimboDimension(entity.world))) {
            chat(entity, new TranslatableText("rifts.destinations.escape.not_in_pocket_dim"));
            return false;
        }
        if (ModDimensions.isLimboDimension(entity.world) && !canEscapeLimbo) {
            chat(entity, new TranslatableText("rifts.destinations.escape.cannot_escape_limbo"));
            return false;
        }

        UUID uuid = entity.getUuid();
        if (uuid != null) {
            Location destLoc = RiftRegistry.instance(entity.world).getOverworldRift(uuid);
            if (destLoc != null && destLoc.getBlockEntity() instanceof RiftBlockEntity || canEscapeLimbo) {
                Location location = VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, entity.getBlockPos())).projectToWorld(false);
                TeleportUtil.teleport(entity, location.world, location.pos, 0);
                return true;
            } else {
                if (destLoc == null) {
                    chat(entity, new TranslatableText("rifts.destinations.escape.did_not_use_rift"));
                } else {
                    chat(entity, new TranslatableText("rifts.destinations.escape.rift_has_closed"));
                }
                //FabricDimensions.teleport(entity, entity.getServer().getWorld(ModDimensions.LIMBO));
                return true;
            }
        } else {
            return false; // No escape info for that entity
        }
    }
}
