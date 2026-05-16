package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.compat.sable.SableHelper;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

public class LimboTarget extends VirtualTarget<LimboTarget> implements EntityTarget {
    public static final LimboTarget INSTANCE = new LimboTarget();

    private LimboTarget() {}

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        BlockPos teleportPos = BlockPos.containing(SableHelper.INSTANCE.projectFrom(entity.level(), entity.position()));
        while (ModDimensions.LIMBO_DIMENSION.getBlockState(VirtualLocation.getTopPos(ModDimensions.LIMBO_DIMENSION, teleportPos.getX(), teleportPos.getZ())).getBlock() == ModBlocks.ETERNAL_FLUID) {
            teleportPos = teleportPos.offset(1, 0, 1);
        }
        TeleportUtil.teleport(entity, ModDimensions.LIMBO_DIMENSION, teleportPos.atY(255), relativeAngle, relativeVelocity);
        return true;
    }

    @Override
    public VirtualTargetType<LimboTarget> getType() {
        return VirtualTargetType.LIMBO;
    }

    @Override
    public LimboTarget copy() {
        return INSTANCE;
    }
}
