package org.dimdev.dimdoors.compat.sable;

import org.dimdev.dimdoors.util.LevelSpaceHelper;

/**
 * Entry point for Sable integration.
 *
 * <p>{@link #HELPER} is the single instance shared by the Sable mixins and by DimDoors' level-space
 * abstraction. Sable-only behavior is reached through it directly rather than through
 * {@link LevelSpaceHelper#INSTANCE}, so concepts that mean nothing outside Sable stay off the shared
 * abstraction.</p>
 */
public class SableCompat {
    /**
     * The Sable level-space helper. Stateless, so it is safe to hold before {@link #init()} runs.
     */
    public static final SableLevelSpaceHelper HELPER = new SableLevelSpaceHelper();

    public static void init() {
        LevelSpaceHelper.INSTANCE = HELPER;
    }
}
