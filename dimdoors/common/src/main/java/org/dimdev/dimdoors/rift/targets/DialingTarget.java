package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.DialingAddress;
import org.dimdev.dimdoors.rift.registry.DialingRegistry;
import org.dimdev.dimdoors.world.pocket.DialingPocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimcore.api.util.EntityUtils;

import java.util.UUID;

import static org.dimdev.dimdoors.DimensionalDoors.LOGGER;

public interface DialingTarget extends PlayerTrackingEntranceTarget<DialingAddress, DialingPocket, DialingRegistry> {
    DialingAddress getAddress();

    @Override
    default boolean processEntity(DialingPocket pocket, EntityTarget target, Entity entity, UUID uuid, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity) {
        boolean received = target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
        if (received) {
            if (this instanceof org.dimdev.dimdoors.block.entity.Rift sourceRift && !sourceRift.isRegistered()) {
                LOGGER.warn("Dialing source at {} was not registered before setting return exit; registering now.", this.getLocation());
                sourceRift.register();
            }

            var regsitry = getSubsystem();

            regsitry.setPlayerAddress(uuid, getAddress());
            regsitry.setExit(uuid, this.getLocation());
        }
        return received;
    }

    @Override
    default DialingRegistry getSubsystem() {
        return DialingRegistry.getInstance();
    }

    @Override
    default DialingAddress getKey(UUID uuid) {
        return getAddress();
    }

    @Override
    default DialingPocket createPocket(VirtualLocation virtualLocation) {
        var pocket = PocketGenerator.generateDialingPocket(new VirtualLocation(virtualLocation.getWorld(), virtualLocation.getX(), virtualLocation.getZ(), -1), getAddress());

        if(pocket instanceof DialingPocket dialingPocket) return dialingPocket;
        return null;
    }

    default @Override Class<DialingPocket> getPocketClass() {
        return DialingPocket.class;
    }

    @Override
    default void onInPocketType(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        EntityUtils.chat(entity, Component.translatable("rifts.destinations.dialing.cant_use_dialing_door_in_dialing_pocket"));
    }
}
