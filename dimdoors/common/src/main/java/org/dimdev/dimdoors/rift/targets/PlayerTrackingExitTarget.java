package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.rift.target.TargetResolver;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.rift.registry.PlayerTrackingSubSystem;
import org.dimdev.dimdoors.rift.registry.PocketRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimcore.api.util.EntityUtils;

import java.util.UUID;

public abstract class PlayerTrackingExitTarget<T extends PlayerTrackingExitTarget<T, S>, S extends PlayerTrackingSubSystem<?, ?, ?>> extends VirtualTarget<T> implements EntityTarget {

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        // TODO: make this recursive
        UUID uuid = EntityUtils.getOwnerPlayerUuid(entity);
        if (uuid == null) {
            return false; // Non-player/owned entity tried to escape/leave private pocket
        }

        var registry = getSubsystem();

        Location destLoc = registry.getExitLocation(uuid);
        Pocket<?, ?> pocket = registry.getPocketFromPlayer(uuid);
        if (registry.isCorrectDimensionForPocket(this.location.getWorld()) && pocket != null) {
            Pocket<?, ?> currentPocket = PocketRegistry.getInstance().getPocketDirectory(pocket.getWorld()).getPocketAt(this.location.pos);
            if (pocket.equals(currentPocket)) {
                registry.setEntrance(uuid, this.location); // Remember which exit was used for next time the pocket is entered
            }
        }

        EntityTarget target = TargetResolver.entity(destLoc);

        if (target == null) {
            if (destLoc == null) {
                EntityUtils.chat(entity, Component.translatable("rifts.destinations.private_pocket_exit.did_not_use_rift"));
            } else {
                EntityUtils.chat(entity, Component.translatable("rifts.destinations.private_pocket_exit.rift_has_closed"));
            }

            LimboTarget.INSTANCE.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location);

            return false;
        }

        return target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, destLoc);
    }

    @Override
    public void register() {
        super.register();
        PocketDirectory registry = PocketRegistry.getInstance().getPocketDirectory(this.location.world);
        Pocket<?, ?> pocket = registry.getPocketAt(this.location.pos);
        if (pocket != null) {
            PocketRegistry.getInstance().addPocketEntrance(pocket, this.location);
        }
    }

    abstract public S getSubsystem();
}
