package org.dimdev.dimdoors.entity.mask;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

public class MaskContaints {
    static final byte MODE_GUARD = 0;
    static final byte MODE_PATROL = 1;
    static final byte MODE_WANDER = 2;
    static final byte MODE_CHASE = 3;
    static final byte MODE_SPOTTING = 4;
    // Detection/acquisition range (used by canSeePlayer / targeting)
    static final double MAX_DETECTION_DISTANCE_SQ = 36.0;
    // Chase leash range (used only to abort chase + teleport home)
    static final double TELEPORT_BACK_DISTANCE_SQ = 2500.0;
    static final int SPOTTING_DURATION_TICKS = 33;
    static final double CATCH_DISTANCE_SQ = 2.25;
    private static final int CHASE_BLOCK_BREAK_INTERVAL = 6;
    static final double BLOCK_BREAK_PROBE_DISTANCE = 0.8;
    static final int GUARD_WALL_CHECK_DISTANCE = 3;
    static final int GUARD_CANDIDATE_ATTEMPTS = 8;
    static final int WANDER_WALL_CLEARANCE = 2;
    static final int WANDER_CANDIDATE_ATTEMPTS = 8;
    static final int PATROL_BLOCK_BREAK_INTERVAL = 6;
    static final double PATROL_BLOCK_BREAK_PROBE_DISTANCE = 0.6;
    static final double CHASE_MAX_HORIZONTAL_SPEED = 0.12;
    static final double CHASE_ACCEL_PER_TICK = 0.01;
    static final int PASSIVE_SCAN_INTERVAL_TICKS = 10;
    static final int LOST_SIGHT_GIVE_UP_TICKS = 60;
    static final double ALERT_RADIUS_SQ = 100.0;
    static final int SPOTTING_LOST_SIGHT_CANCEL_TICKS = 10;
    static final int MAX_CHASE_TICKS = 400;
    static final int PATROL_PAUSE_DURATION_TICKS = 40;
}
