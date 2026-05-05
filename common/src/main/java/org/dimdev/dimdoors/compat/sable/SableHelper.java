package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class SableHelper {
    public static SableHelper INSTANCE = new SableHelper();

    public BlockPos projectFrom(Level level, BlockPos pos) {
        return pos;
    }

    public Vec3 projectFrom(Level level, Vec3 pos) {
        return pos;
    }

    public Vec3 projectTo(ServerLevel level, Vec3 pos) {
        return pos;
    }

    public void projectTo(ServerLevel level, Vector3d pos) {}

    public AfterBlockData getAfterBlockData(Entity entity, AABB box, Vec3 previousPos, Vec3 currentPos) {
        return new AfterBlockData(box, previousPos, currentPos);
    }

    public record AfterBlockData(AABB box, Vec3 previousPos, Vec3 currentPos) {

    }
}