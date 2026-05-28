package org.dimdev.dimdoors.entity.mask;

final class MaskConstants {
    private MaskConstants() {
    }

    static final double CHASE_RANGE = 50.0;
    static final double CATCH_DISTANCE_SQ = 1.35 * 1.35;

    static final double CHASE_SPEED = 0.125;
    static final double CHASE_SOLID_SPEED = 0.045;
    static final double PASSIVE_SPEED = 0.055;

    static final int PASSIVE_SCAN_INTERVAL = 5;
    static final int PATROL_PAUSE_TICKS = 45;
    static final int STUN_TICKS = 60;
    static final int MAX_WAYPOINTS = 10;

    static final double BLOCK_ALERT_RANGE = 5.0;
    static final double ECHO_BLOCK_NOTICE_RANGE = 10.0;

    static final int DETECTION_BUBBLE_INTERVAL = 12;
}
