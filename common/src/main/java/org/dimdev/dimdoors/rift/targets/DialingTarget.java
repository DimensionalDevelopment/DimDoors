package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.DialingAddress;
import org.dimdev.dimdoors.rift.registry.DialingRegistry;
import org.dimdev.dimdoors.rift.registry.PocketRegistry;
import org.dimdev.dimdoors.world.pocket.DialingPocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.limlib.api.util.EntityUtils;

import java.util.UUID;

import static org.dimdev.dimdoors.DimensionalDoors.LOGGER;

public interface DialingTarget extends EntityTarget {
    DialingAddress getAddress();
    Location getLocation();

    @Override
    default boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        UUID uuid = EntityUtils.getOwnerPlayerUuid(entity);
        if (uuid == null) {
            return false;
        }

        DialingRegistry registry = DialingRegistry.getInstance();
        VirtualLocation virtualLocation = VirtualLocation.fromLocation(this.getLocation());
        DialingPocket pocket = registry.getDialingPocket(getAddress());
        if (pocket == null) {
            pocket = this.generateDialingPocket(uuid, virtualLocation);
            if (pocket == null) {
                return false;
            }
        }

        registry.setPlayerAddress(uuid, getAddress());
        Location destLoc = registry.resolveEntrance(uuid);
        if (destLoc == null) {
            LOGGER.info("All entrances are gone, creating a new dialing pocket!");
            pocket = generateDialingPocket(uuid, virtualLocation);
            if (pocket == null) {
                return false;
            }
            destLoc = PocketRegistry.getInstance().getPocketEntrance(pocket);
        }

        if (destLoc == null || !(destLoc.getBlockEntity() instanceof EntityTarget target)) {
            LOGGER.error("Could not enter dialing pocket {} for {} because no valid entrance is registered.", pocket.getId(), getAddress());
            this.sendMissingEntranceHint(entity, pocket);
            return false;
        }

        boolean received = target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
        if (received) {
            if (this instanceof org.dimdev.dimdoors.block.entity.Rift sourceRift && !sourceRift.isRegistered()) {
                LOGGER.warn("Dialing source at {} was not registered before setting return exit; registering now.", this.getLocation());
                sourceRift.register();
            }
            registry.setPlayerAddress(uuid, getAddress());
            registry.setExit(uuid, this.getLocation());
        }
        return received;
    }

    default DialingPocket generateDialingPocket(UUID uuid, VirtualLocation virtualLocation) {
        Pocket<?, ?> generatedPocket = PocketGenerator.generateDialingPocket(new VirtualLocation(virtualLocation.getWorld(), virtualLocation.getX(), virtualLocation.getZ(), -1), getAddress());

        if (generatedPocket instanceof DialingPocket pocket) {
            Location entrance = PocketRegistry.getInstance().getPocketEntrance(pocket);
            if (entrance == null) {
                LOGGER.error("Could not create dialing pocket {} for {} because no entrance was registered.", pocket.getId(), uuid);
                return null;
            }

            DialingRegistry registry = DialingRegistry.getInstance();
            registry.setEntrance(uuid, null);
            registry.setExit(uuid, null);
            registry.setDialingPocketAddress(getAddress(), pocket);
            registry.setPlayerAddress(uuid, getAddress());
            return pocket;
        } else {
            LOGGER.error("Could not create dialing pocket for {} because generation returned {}.", uuid, generatedPocket == null ? "null" : generatedPocket.getClass().getSimpleName());
            return null;
        }
    }

    private void sendMissingEntranceHint(Entity entity, DialingPocket pocket) {
        Player owner = EntityUtils.getOwnerPlayer(entity);
        if (owner == null) {
            return;
        }

        BlockPos origin = pocket.getOrigin();
        VirtualLocation virtualLocation = pocket.virtualLocation;
        EntityUtils.chat(owner, Component.literal(String.format(
                "Dialing pocket entrance missing. Pocket origin: %s @ %d, %d, %d. Virtual coords: x=%d, z=%d, depth=%d.",
                pocket.getWorld().location(),
                origin.getX(),
                origin.getY(),
                origin.getZ(),
                virtualLocation.getX(),
                virtualLocation.getZ(),
                virtualLocation.getDepth()
        )));
    }
}
