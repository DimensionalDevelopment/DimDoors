package org.dimdev.dimdoors.compat.sable;

import org.dimdev.dimdoors.util.LevelSpaceHelper;

public class SableCompat {
    public static void init() {
        LevelSpaceHelper.INSTANCE = new SableLevelSpaceHelper();
    }
}
