package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.compat.sable.SableHelper;
import org.dimdev.dimdoors.rift.registry.DialingRegistry;
import org.dimdev.dimdoors.rift.registry.PocketRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.limlib.api.util.EntityUtils;

import java.util.UUID;

public class DialingExitTarget extends VirtualTarget<DialingExitTarget> implements EntityTarget {
    public static final DialingExitTarget INSTANCE = new DialingExitTarget();
    public static final RGBA COLOR = new RGBA(0, 1, 0, 1);

    private DialingExitTarget() {
    }

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        // TODO: make this recursive
        UUID uuid = EntityUtils.getOwnerPlayerUuid(entity);
        if (uuid == null) {
            return false;
        }

        Location destLoc = DialingRegistry.getInstance().getExitLocation(uuid);
        Pocket<?, ?> pocket = DialingRegistry.getInstance().getDialingPocket(uuid);
        if (ModDimensions.isPocketDimension(this.location.getWorld()) && pocket != null) {
            Pocket<?, ?> currentPocket = PocketRegistry.getInstance().getPocketDirectory(pocket.getWorld()).getPocketAt(this.location.pos);
            if (pocket.equals(currentPocket)) {
                DialingRegistry.getInstance().setEntrance(uuid, this.location); // Remember which exit was used for next time the pocket is entered
            }
        }

        EntityTarget target = this.resolveDestinationTarget(destLoc);
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

    private EntityTarget resolveDestinationTarget(Location location) {
        if (location == null) {
            return null;
        }

        ServerLevel level = location.getWorld();
        if (level == null) {
            return null;
        }

        SableHelper.INSTANCE.ensureSableSubLevelLoaded(level, location.pos);
        EntityTarget target = Targets.resolveBlockStateEntityTarget(level, location.pos);
        if (target != null) {
            return target;
        }

        return location.asTarget().as(Targets.ENTITY);
    }

    @Override
    public void register() {
        super.register();
        PocketDirectory privatePocketRegistry = PocketRegistry.getInstance().getPocketDirectory(this.location.world);
        Pocket<?, ?> pocket = privatePocketRegistry.getPocketAt(this.location.pos);
        if (pocket != null) {
            PocketRegistry.getInstance().addPocketEntrance(pocket, this.location);
        }
    }

    @Override
    public VirtualTargetType<DialingExitTarget> getType() {
        return VirtualTargetType.DIALING_EXIT;
    }

    @Override
    public DialingExitTarget copy() {
        return this;
    }
}
