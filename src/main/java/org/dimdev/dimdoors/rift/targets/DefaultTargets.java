package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.util.InstanceMap;

public final class DefaultTargets {
	private static final InstanceMap DEFAULT_TARGETS = new InstanceMap();

	public static <T extends Target> T getDefaultTarget(Class<T> type) {
		if (DEFAULT_TARGETS.containsKey(type)) {
			return DEFAULT_TARGETS.get(type);
		} else {
			throw new RuntimeException("No default target for " + type.getName() + " registered");
		}
	}

	public static <T extends Target, U extends T> void registerDefaultTarget(Class<T> type, U impl) {
		DEFAULT_TARGETS.put(type, impl);
	}
}
