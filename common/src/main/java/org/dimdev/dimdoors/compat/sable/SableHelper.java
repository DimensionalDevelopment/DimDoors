package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.rift.registry.Rift;
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

    public boolean isMissingSablePlotHolder(ServerLevel level, BlockPos pos) {
        return false;
    }

    public void validateTeleportDestination(ServerLevel level, Vec3 pos) {
    }

    public boolean prepareRiftCreation(ServerLevel level, BlockPos pos) {
        return true;
    }

    public void prepareCrossDimensionTeleport(Entity entity, ServerLevel destination) {
    }

    public void updateRiftTrackingPoint(ServerLevel level, Rift rift) {
    }

    public void removeRiftTrackingPoint(ServerLevel level, Rift rift) {
    }

    public TeleportFrame projectTeleportFrame(ServerLevel level, Vec3 pos, Rotations angle, Vec3 velocity) {
        return new TeleportFrame(pos, angle, velocity);
    }

    public TeleportFrame projectTeleportFrame(ServerLevel level, Location location, Vec3 pos, Rotations angle, Vec3 velocity) {
        return projectTeleportFrame(level, pos, angle, velocity);
    }

    public TeleportFrame sourceTeleportFrame(ServerLevel level, BlockPos sourcePos, Entity entity, Vec3 pos, Rotations angle, Vec3 velocity) {
        return new TeleportFrame(pos, angle, velocity);
    }

    public AfterBlockData getAfterBlockData(Entity entity, AABB box, Vec3 previousPos, Vec3 currentPos) {
        return new AfterBlockData(box, previousPos, currentPos);
    }

    public record AfterBlockData(AABB box, Vec3 previousPos, Vec3 currentPos) {

    }

    public record TeleportFrame(Vec3 pos, Rotations angle, Vec3 velocity) {

    }
}
