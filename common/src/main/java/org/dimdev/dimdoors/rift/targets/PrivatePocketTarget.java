package org.dimdev.dimdoors.rift.targets;

import dev.eriksonn.aeronautics.index.AeroTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PocketColor;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.Arrays;
import java.util.UUID;

//TODO: add the ability to do addon spefific EntityTarget stuff and use it seperate dyeable from PrivatePocket
public class PrivatePocketTarget extends VirtualTarget<PrivatePocketTarget> implements EntityTarget {
    public static final PrivatePocketTarget INSTANCE = new PrivatePocketTarget();
    private static final Logger LOGGER = LogManager.getLogger();

    public static final RGBA COLOR = new RGBA(0, 1, 0, 1);

    private PrivatePocketTarget() {
    }

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        UUID uuid = EntityUtils.getOwnerPlayerUuid(entity);
        if (uuid == null) {
            return false;
        }

        VirtualLocation virtualLocation = VirtualLocation.fromLocation(this.location);
        PrivatePocket pocket = DimensionalRegistry.getPrivateRegistry().getPrivatePocket(uuid);
        if (pocket == null) {
            pocket = this.generatePrivatePocket(uuid, virtualLocation);
            if (pocket == null) {
                return false;
            }
        }

        Location destLoc = DimensionalRegistry.getRiftRegistry().getPrivatePocketEntrance(uuid);
        if (destLoc == null) {
            destLoc = DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket);
        }
        if (destLoc == null) {
            LOGGER.info("All entrances are gone, creating a new private pocket!");
            pocket = this.generatePrivatePocket(uuid, virtualLocation);
            if (pocket == null) {
                return false;
            }
            destLoc = DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket);
        }

        return this.processEntity(pocket, destLoc, entity, uuid, relativePos, relativeAngle, relativeVelocity);
    }

    private boolean processEntity(PrivatePocket pocket, Location destLoc, Entity entity, UUID uuid, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity) {
        if (destLoc == null || !(destLoc.getBlockEntity() instanceof EntityTarget target)) {
            LOGGER.error("Could not enter private pocket {} for {} because no valid entrance is registered.", pocket.getId(), uuid);
            this.sendMissingEntranceHint(entity, pocket);
            return false;
        }

        if (entity instanceof ItemEntity itemEntity) {
            var stack = itemEntity.getItem();

            var dye = PocketColor.from(stack);

            if (dye != null) {
                var dyeableAddon = pocket.getAddon(PocketAddon.PocketAddonType.DYEABLE_ADDON).orElse(null);

                if (dyeableAddon != null) {
                    var remaining = dyeableAddon.addDye(pocket, EntityUtils.getOwner(entity), dye, stack.getCount());

                    if(remaining <= 0) {
                        entity.discard();
                    } else {
                        stack.setCount(remaining);
                    }
                    return true;
                } else {
                    return target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
                }
            } else {
                return target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
            }
        } else {
            boolean received = target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
            if (received) {
                DimensionalRegistry.getRiftRegistry().setLastPrivatePocketExit(uuid, this.location);
            }
            return received;
        }
    }

    @Override
    public VirtualTargetType<PrivatePocketTarget> getType() {
        return VirtualTargetType.PRIVATE;
    }

    @Override
    public PrivatePocketTarget copy() {
        return this;
    }

    private PrivatePocket generatePrivatePocket(UUID uuid, VirtualLocation virtualLocation) {
        Pocket<?, ?> generatedPocket = PocketGenerator.generatePrivatePocketV2(new VirtualLocation(virtualLocation.getWorld(), virtualLocation.getX(), virtualLocation.getZ(), -1));
        if (!(generatedPocket instanceof PrivatePocket pocket)) {
            LOGGER.error("Could not create private pocket for {} because generation returned {}.", uuid, generatedPocket == null ? "null" : generatedPocket.getClass().getSimpleName());
            return null;
        }

        Location entrance = DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket);
        if (entrance == null) {
            LOGGER.error("Could not create private pocket {} for {} because no entrance was registered.", pocket.getId(), uuid);
            return null;
        }

        DimensionalRegistry.getRiftRegistry().setLastPrivatePocketEntrance(uuid, null);
        DimensionalRegistry.getRiftRegistry().setLastPrivatePocketExit(uuid, null);
        DimensionalRegistry.getPrivateRegistry().setPrivatePocketID(uuid, pocket);
        return pocket;
    }

    private void sendMissingEntranceHint(Entity entity, PrivatePocket pocket) {
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
}
