package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.entity.mask.MaskEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MaskHomeBlockEntity extends BlockEntity {
    private static final String WAYPOINT_COUNT_KEY = "WaypointCount";
    private static final String WAYPOINT_KEY = "Waypoint";
    private static final String BOUND_MASK_KEY = "BoundMask";
    private static final int ROUTE_PARTICLE_INTERVAL = 4;

    private final List<BlockPos> relativeWaypoints = new ArrayList<>();
    private UUID boundMaskId;
    private int routeDisplayTicks;

    public MaskHomeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.MASK_HOME, pos, state);
    }

    public void configure(List<BlockPos> absoluteWaypoints, UUID maskId) {
        replaceWaypoints(absoluteWaypoints);
        this.boundMaskId = maskId;
        setChanged();
    }

    public void replaceWaypoints(List<BlockPos> absoluteWaypoints) {
        relativeWaypoints.clear();
        for (BlockPos waypoint : absoluteWaypoints) {
            relativeWaypoints.add(new BlockPos(
                    waypoint.getX() - worldPosition.getX(),
                    waypoint.getY() - worldPosition.getY(),
                    waypoint.getZ() - worldPosition.getZ()
            ));
        }
        updateBoundMaskRoute();
        setChanged();
    }

    public List<BlockPos> getAbsoluteWaypoints() {
        return relativeWaypoints.stream()
                .map(offset -> worldPosition.offset(offset.getX(), offset.getY(), offset.getZ()))
                .toList();
    }

    public void showRoute(int ticks) {
        routeDisplayTicks = ticks;
    }

    public void onHomeDestroyed() {
        if (!(level instanceof ServerLevel serverLevel) || boundMaskId == null) {
            return;
        }

        Entity entity = serverLevel.getEntity(boundMaskId);
        if (entity instanceof MaskEntity mask) {
            mask.detachFromDestroyedHome(worldPosition);
        }
    }

    private void updateBoundMaskRoute() {
        if (!(level instanceof ServerLevel serverLevel) || boundMaskId == null) {
            return;
        }

        Entity entity = serverLevel.getEntity(boundMaskId);
        if (entity instanceof MaskEntity mask) {
            mask.replaceHomeWaypoints(worldPosition, getAbsoluteWaypoints());
        }
    }

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, MaskHomeBlockEntity home) {
        if (home.routeDisplayTicks <= 0) {
            return;
        }

        home.routeDisplayTicks--;
        if (home.routeDisplayTicks % ROUTE_PARTICLE_INTERVAL != 0) {
            return;
        }

        List<BlockPos> route = home.getDisplayRoute();
        for (BlockPos point : route) {
            level.sendParticles(ParticleTypes.END_ROD, point.getX() + 0.5, point.getY() + 1.0, point.getZ() + 0.5, 4, 0.08, 0.08, 0.08, 0.01);
        }

        for (int i = 1; i < route.size(); i++) {
            emitLine(level, route.get(i - 1), route.get(i));
        }
    }

    private List<BlockPos> getDisplayRoute() {
        List<BlockPos> waypoints = getAbsoluteWaypoints();
        List<BlockPos> route = new ArrayList<>();
        route.add(worldPosition);
        for (int i = waypoints.size() - 1; i >= 0; i--) {
            route.add(waypoints.get(i));
        }
        return route;
    }

    private static void emitLine(ServerLevel level, BlockPos from, BlockPos to) {
        Vec3 start = Vec3.atCenterOf(from).add(0.0, 0.5, 0.0);
        Vec3 end = Vec3.atCenterOf(to).add(0.0, 0.5, 0.0);
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        int samples = Math.max(1, (int) Math.floor(length * 2.0));
        Vec3 step = delta.scale(1.0 / samples);
        for (int i = 1; i < samples; i++) {
            Vec3 point = start.add(step.scale(i));
            level.sendParticles(ParticleTypes.WAX_ON, point.x, point.y, point.z, 1, 0.02, 0.02, 0.02, 0.0);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(WAYPOINT_COUNT_KEY, relativeWaypoints.size());
        for (int i = 0; i < relativeWaypoints.size(); i++) {
            tag.putLong(WAYPOINT_KEY + i, relativeWaypoints.get(i).asLong());
        }
        if (boundMaskId != null) {
            tag.putUUID(BOUND_MASK_KEY, boundMaskId);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        relativeWaypoints.clear();
        int waypointCount = tag.getInt(WAYPOINT_COUNT_KEY);
        for (int i = 0; i < waypointCount; i++) {
            String key = WAYPOINT_KEY + i;
            if (tag.contains(key)) {
                relativeWaypoints.add(BlockPos.of(tag.getLong(key)));
            }
        }
        boundMaskId = tag.hasUUID(BOUND_MASK_KEY) ? tag.getUUID(BOUND_MASK_KEY) : null;
    }
}
