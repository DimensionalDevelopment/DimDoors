package org.dimdev.dimdoors.api.util;

import java.util.HashMap;
import java.util.Map;

public class InstanceMap { // Type safe map between classes and instances
	private final Map<Class<?>, Object> uncheckedMap = new HashMap<>();

	public InstanceMap() {
	}

	public <T> void put(Class<T> key, T value) {
		this.uncheckedMap.put(key, value);
	}

	public <T> T get(Class<T> key) {
		return key.cast(this.uncheckedMap.get(key));
	}

	public <T> T remove(Class<T> key) {
		return key.cast(this.uncheckedMap.remove(key));
	}

	public void clear() {
		this.uncheckedMap.clear();
	}

	public boolean containsKey(Class<?> key) {
		return this.uncheckedMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return this.uncheckedMap.containsValue(value);
	}
}
