package org.dimdev.dimdoors.api.rift.target;

import java.util.Optional;

import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.InstanceMap;

public final class DefaultTargets {
	private static final InstanceMap DEFAULT_TARGETS = new InstanceMap();

	public static <T extends Target> T getDefaultTarget(Class<T> type) {
		return Optional.ofNullable(DEFAULT_TARGETS.get(type))
				.orElseThrow(() ->  new RuntimeException("No default target for " + type.getCanonicalName() + " registered"));
	}

	public static <T extends Target, U extends T> void registerDefaultTarget(Class<T> type, U impl) {
		DEFAULT_TARGETS.put(type, impl);
	}
}
