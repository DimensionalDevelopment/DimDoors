package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Rotations;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.PocketColor;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;
import org.dimdev.dimcore.api.util.EntityUtils;

import java.util.UUID;

//TODO: add the ability to do addon spefific EntityTarget stuff and use it seperate dyeable from PrivatePocket
public class PrivatePocketTarget extends VirtualTarget<PrivatePocketTarget> implements PlayerTrackingEntranceTarget<UUID, PrivatePocket, PrivateRegistry> {
    public static final PrivatePocketTarget INSTANCE = new PrivatePocketTarget();

    private PrivatePocketTarget() {
    }

    public boolean processEntity(PrivatePocket pocket, EntityTarget target, Entity entity, UUID uuid, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity) {

        if (entity instanceof ItemEntity itemEntity) {
            var stack = itemEntity.getItem();

            var dye = PocketColor.from(stack);

            if (dye == null) {
                return target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
            } else {
                var dyeableAddon = pocket.getAddon(PocketAddon.PocketAddonType.DYEABLE_ADDON).orElse(null);

                if (dyeableAddon == null) {
                    return target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
                } else {
                    var remaining = dyeableAddon.addDye(pocket, EntityUtils.getOwner(entity), dye, stack.getCount());

                    if(remaining <= 0) {
                        entity.discard();
                    } else {
                        stack.setCount(remaining);
                    }
                    return true;
                }
            }
        } else {
            boolean received = target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, null);
            if (received) {
                PrivateRegistry.getInstance().setExit(uuid, this.location);
            }
            return received;
        }
    }

    @Override
    public VirtualTarget.VirtualTargetType<PrivatePocketTarget> getType() {
        return VirtualTarget.VirtualTargetType.PRIVATE;
    }

    @Override
    public PrivatePocketTarget copy() {
        return this;
    }

    @Override
    public PrivateRegistry getSubsystem() {
        return PrivateRegistry.getInstance();
    }

    @Override
    public Class<PrivatePocket> getPocketClass() {
        return PrivatePocket.class;
    }

    @Override
    public UUID getKey(UUID uuid) {
        return uuid;
    }

    @Override
    public PrivatePocket createPocket(VirtualLocation virtualLocation) {
        var pocket = PocketGenerator.generatePrivatePocketV2(new VirtualLocation(virtualLocation.getWorld(), virtualLocation.getX(), virtualLocation.getZ(), -1));

        if(pocket instanceof PrivatePocket privatePocket) return privatePocket;
        return null;
    }
}
