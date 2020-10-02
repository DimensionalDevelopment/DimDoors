package org.dimdev.dimdoors.rift.targets;

import java.util.UUID;

import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.TeleportUtil;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import static org.dimdev.dimdoors.util.EntityUtils.chat;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class EscapeTarget extends VirtualTarget implements EntityTarget { // TODO: createRift option
    public static final Codec<EscapeTarget> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.BOOL.fieldOf("canEscapeLimbo").forGetter(target -> target.canEscapeLimbo)
        ).apply(instance, EscapeTarget::new);
    });

    protected boolean canEscapeLimbo = false;

    public EscapeTarget(boolean canEscapeLimbo) {
        this.canEscapeLimbo = canEscapeLimbo;
    }

    @Override
    public boolean receiveEntity(Entity entity, float yawOffset) {
        if (!ModDimensions.isDimDoorsPocketDimension(entity.world) && !(ModDimensions.isLimboDimension(entity.world))) {
            chat(entity, new TranslatableText("rifts.destinations.escape.not_in_pocket_dim"));
            return false;
        }
        if (ModDimensions.isLimboDimension(entity.world) && !this.canEscapeLimbo) {
            chat(entity, new TranslatableText("rifts.destinations.escape.cannot_escape_limbo"));
            return false;
        }

        UUID uuid = entity.getUuid();
        if (uuid != null) {
            Location destLoc = RiftRegistry.instance().getOverworldRift(uuid);
            if (destLoc != null && destLoc.getBlockEntity() instanceof RiftBlockEntity || this.canEscapeLimbo) {
                Location location = VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, entity.getBlockPos())).projectToWorld(false);
                TeleportUtil.teleport(entity, location.getWorld(), location.getBlockPos(), 0);
            } else {
                if (destLoc == null) {
                    chat(entity, new TranslatableText("rifts.destinations.escape.did_not_use_rift"));
                } else {
                    chat(entity, new TranslatableText("rifts.destinations.escape.rift_has_closed"));
                }
                if (!entity.getEntityWorld().isClient) {
                    if (ModDimensions.LIMBO_DIMENSION != null) {
                        Entity newEntity = entity.moveToWorld(ModDimensions.LIMBO_DIMENSION);
                        newEntity.setPos(this.location.getX(), this.location.getY(), this.location.getZ());
                    }
                }
            }
            return true;
        } else {
            return false; // No escape info for that entity
        }
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.ESCAPE;
    }
}
