package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.compat.sable.SableHelper;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.UUID;

public class PrivatePocketExitTarget extends VirtualTarget<PrivatePocketExitTarget> implements EntityTarget {
    public static final PrivatePocketExitTarget INSTANCE = new PrivatePocketExitTarget();
    public static final RGBA COLOR = new RGBA(0, 1, 0, 1);

    private PrivatePocketExitTarget() {
    }

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        // TODO: make this recursive
        UUID uuid = EntityUtils.getOwnerPlayerUuid(entity);
        if (uuid == null) {
            return false; // Non-player/owned entity tried to escape/leave private pocket
        }

        Location destLoc = DimensionalRegistry.getRiftRegistry().getPrivatePocketExit(uuid);
        Pocket<?, ?> pocket = DimensionalRegistry.getPrivateRegistry().getPrivatePocket(uuid);
        if (ModDimensions.isPrivatePocketDimension(this.location.getWorld()) && pocket != null) {
            Pocket<?, ?> currentPocket = DimensionalRegistry.getPocketDirectory(pocket.getWorld()).getPocketAt(this.location.pos);
            if (pocket.equals(currentPocket)) {
                DimensionalRegistry.getRiftRegistry().setLastPrivatePocketEntrance(uuid, this.location); // Remember which exit was used for next time the pocket is entered
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
        return this.resolveDestinationTarget(level, location.pos);
    }

    private EntityTarget resolveDestinationTarget(ServerLevel level, BlockPos pos) {
        EntityTarget target = this.resolveDestinationTargetAt(level, pos);
        if (target != null) {
            return target;
        }

        target = this.resolveDestinationTargetAt(level, pos.below());
        if (target != null) {
            return target;
        }

        return this.resolveDestinationTargetAt(level, pos.above());
    }

    private EntityTarget resolveDestinationTargetAt(ServerLevel level, BlockPos pos) {
        if (SableHelper.INSTANCE.getBlockEntity(level, pos) instanceof EntityTarget target) {
            return target;
        }

        return Targets.resolveBlockStateEntityTarget(level, pos);
    }

    @Override
    public void register() {
        super.register();
        PocketDirectory privatePocketRegistry = DimensionalRegistry.getPocketDirectory(this.location.world);
        Pocket<?, ?> pocket = privatePocketRegistry.getPocketAt(this.location.pos);
        if (pocket != null) {
            DimensionalRegistry.getRiftRegistry().addPocketEntrance(pocket, this.location);
        }
    }

    @Override
    public VirtualTargetType<PrivatePocketExitTarget> getType() {
        return VirtualTargetType.PRIVATE_POCKET_EXIT;
    }

    @Override
    public PrivatePocketExitTarget copy() {
        return this;
    }
}
