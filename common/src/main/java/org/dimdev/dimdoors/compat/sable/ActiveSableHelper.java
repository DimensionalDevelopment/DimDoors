package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class ActiveSableHelper extends SableHelper {
    private static Vector3d scratch = new Vector3d();

    @Override
    public Vec3 projectFrom(Level level, Vec3 pos) {
        return SableCompanion.INSTANCE.projectOutOfSubLevel(level, pos);
    }

    @Override
    public BlockPos projectFrom(Level level, BlockPos pos) {
        scratch.set(pos.getX(), pos.getY(), pos.getZ());
        SableCompanion.INSTANCE.projectOutOfSubLevel(level, scratch);
        return BlockPos.containing(scratch.x(), scratch.y(), scratch.z());
    }

    @Override
    public Vec3 projectTo(ServerLevel level, Vec3 pos) {
        var subLevel = SableCompanion.INSTANCE.getContaining(level, pos);

        if(subLevel != null) {
            return subLevel.logicalPose().transformPositionInverse(pos);
        }

        return pos;
    }

    @Override
    public void projectTo(ServerLevel level, Vector3d pos) {
        var subLevel = SableCompanion.INSTANCE.getContaining(level, pos);

        if(subLevel != null) {
            subLevel.logicalPose().transformPositionInverse(pos);
        }
    }

    @Override
    public AfterBlockData getAfterBlockData(Entity entity, AABB box, Vec3 previousPos, Vec3 currentPos) {
        var trackingSubLevel = SableCompanion.INSTANCE.getTrackingSubLevel(entity);
        if (trackingSubLevel != null) {
            // Transform player positions to sub-level plot grid space
            previousPos = trackingSubLevel.logicalPose().transformPositionInverse(previousPos);
            currentPos = trackingSubLevel.logicalPose().transformPositionInverse(currentPos);

            box = new AABB(
                    trackingSubLevel.logicalPose().transformPositionInverse(new Vec3(box.minX, box.minY, box.minZ)),
                    trackingSubLevel.logicalPose().transformPositionInverse(new Vec3(box.maxX, box.maxY, box.maxZ))
            );
        }

        return new AfterBlockData(box, previousPos, currentPos);
    }
}
