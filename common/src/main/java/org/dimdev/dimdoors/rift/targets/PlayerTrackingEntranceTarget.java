package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.rift.target.TargetResolver;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.rift.registry.PlayerTrackingSubSystem;
import org.dimdev.dimdoors.rift.registry.PocketRegistry;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.limlib.api.util.EntityUtils;

import java.util.UUID;

import static org.dimdev.dimdoors.DimensionalDoors.LOGGER;

public interface PlayerTrackingEntranceTarget<O, P extends Pocket<?, ?>, S extends PlayerTrackingSubSystem<O, P, S>> extends EntityTarget {
    S getSubsystem();
    Location getLocation();

    @Override
    default boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        UUID uuid = EntityUtils.getOwnerPlayerUuid(entity);
        if (uuid == null) {
            return false;
        }

        if(isInDialingPocket()) {
            onInPocketType(entity, relativePos, relativeAngle, relativeVelocity, location);
            return true;
        }

        var registry = getSubsystem();

        VirtualLocation virtualLocation = VirtualLocation.fromLocation(getLocation());

        O key = getKey(uuid);

        P pocket = registry.getPocketFromKey(key);
        if (pocket == null) {
            pocket = this.createPocket(key, uuid, virtualLocation);
            if (pocket == null) {
                return false;
            }
        }

        registry.setCurrentKey(uuid, key); // no-op for private, setPlayerAddress for dialing

        Location destLoc = registry.resolveEntrance(uuid);
        if (destLoc == null) {
            LOGGER.info("All entrances are gone, creating a new private pocket!");
            pocket = this.createPocket(key, uuid, virtualLocation);
            if (pocket == null) {
                return false;
            }
            destLoc = PocketRegistry.getInstance().getPocketEntrance(pocket);
        }

        var target = TargetResolver.entity(destLoc);

        if (target == null) {
            LOGGER.error("Could not enter private pocket {} for {} because no valid entrance is registered.", pocket.getId(), uuid);
            this.sendMissingEntranceHint(entity, pocket);
            return false;
        }

        return this.processEntity(pocket, target, entity, uuid, relativePos, relativeAngle, relativeVelocity);
    }

    default void onInPocketType(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {

    }

    default boolean isInDialingPocket() {
        return PocketRegistry.getInstance().getPocketAt(getLocation(), getPocketClass()) != null;
    }

    Class<P> getPocketClass();

    boolean processEntity(P pocket, EntityTarget target, Entity entity, UUID uuid, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity);

    default P createPocket(O key, UUID uuid, VirtualLocation virtualLocation) {
        P pocket = createPocket(virtualLocation);

        if (pocket != null) {
            Location entrance = PocketRegistry.getInstance().getPocketEntrance(pocket);
            if (entrance == null) {
                LOGGER.error("Could not create dialing pocket {} for {} because no entrance was registered.", pocket.getId(), uuid);
                return null;
            }

            var registry = getSubsystem();

            registry.setEntrance(uuid, null);
            registry.setExit(uuid, null);
            
            registry.setNewPocket(uuid, key, pocket);

            return pocket;
        } else {
//            LOGGER.error("Could not create dialing pocket for {} because generation returned {}.", uuid, generatedPocket == null ? "null" : generatedPocket.getClass().getSimpleName());
            return null;
        }
    }

    default void sendMissingEntranceHint(Entity entity, P pocket) {
        Player owner = EntityUtils.getOwnerPlayer(entity);
        if (owner == null) {
            return;
        }

        BlockPos origin = pocket.getOrigin();
        VirtualLocation virtualLocation = pocket.virtualLocation;
        EntityUtils.chat(owner, Component.literal(String.format(
                "Private pocket entrance missing. Pocket origin: %s @ %d, %d, %d. Virtual coords: x=%d, z=%d, depth=%d.",
                pocket.getWorld().location(),
                origin.getX(),
                origin.getY(),
                origin.getZ(),
                virtualLocation.getX(),
                virtualLocation.getZ(),
                virtualLocation.getDepth()
        )));
    }

    O getKey(UUID uuid);

    P createPocket(VirtualLocation virtualLocation);
}
