package org.dimdev.dimdoors.shared.rifts.targets;

import org.dimdev.ddutils.InstanceMap;

public final class DefaultTargets {
    private static final InstanceMap defaultTargets = new InstanceMap();

    public static <T extends ITarget> T getDefaultTarget(Class<T> type) {
        if (defaultTargets.containsKey(type)) return defaultTargets.get(type);
        throw new RuntimeException("No default target for " + type.getName() + " registered");
    }

    public static <T extends ITarget, U extends T> void registerDefaultTarget(Class<T> type, U impl) {
        defaultTargets.put(type, impl);
    }
}
