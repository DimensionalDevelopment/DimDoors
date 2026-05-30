package org.dimdev.dimdoors.entity.mask;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class MaskPatrolRoute {
    private final List<BlockPos> route = new ArrayList<>();

    private int index;
    private int direction = 1;
    private int pauseTicks;

    void clear() {
        route.clear();
        index = 0;
        direction = 1;
        pauseTicks = 0;
    }

    void configure(BlockPos home, List<BlockPos> waypoints) {
        clear();

        if (waypoints.isEmpty()) {
            return;
        }

        route.add(home.immutable());

        int max = Math.min(MaskConstants.MAX_WAYPOINTS, waypoints.size());
        for (int i = max - 1; i >= 0; i--) {
            route.add(waypoints.get(i).immutable());
        }
    }

    boolean canPatrol() {
        return route.size() >= 2;
    }

    @Nullable
    BlockPos currentTarget() {
        if (route.isEmpty()) {
            return null;
        }

        return route.get(Mth.clamp(index, 0, route.size() - 1));
    }

    void tickPausedAtTarget() {
        if (++pauseTicks >= MaskConstants.PATROL_PAUSE_TICKS) {
            pauseTicks = 0;
            advance();
        }
    }

    void resetPause() {
        pauseTicks = 0;
    }

    private void advance() {
        index += direction;

        if (index >= route.size()) {
            index = Math.max(0, route.size() - 2);
            direction = -1;
        } else if (index < 0) {
            index = Math.min(1, route.size() - 1);
            direction = 1;
        }
    }

    void save(CompoundTag tag) {
        tag.putInt("PatrolIndex", index);
        tag.putInt("PatrolDirection", direction);
        tag.putInt("PatrolRouteSize", route.size());

        for (int i = 0; i < route.size(); i++) {
            tag.putLong("PatrolRoute" + i, route.get(i).asLong());
        }
    }

    void load(CompoundTag tag) {
        route.clear();

        index = tag.getInt("PatrolIndex");
        direction = tag.contains("PatrolDirection") ? tag.getInt("PatrolDirection") : 1;
        if (direction == 0) {
            direction = 1;
        }

        pauseTicks = 0;

        int routeSize = tag.getInt("PatrolRouteSize");
        for (int i = 0; i < routeSize; i++) {
            String key = "PatrolRoute" + i;
            if (tag.contains(key)) {
                route.add(BlockPos.of(tag.getLong(key)));
            }
        }
    }
}
