package org.dimdev.util;

import java.util.HashMap;
import java.util.Map;

public class InstanceMap { // Type safe map between classes and instances
    public InstanceMap() {}

    private final Map<Class<?>, Object> uncheckedMap = new HashMap<>();

    public <T> void put(Class<T> key, T value) {
        uncheckedMap.put(key, value);
    }

    public <T> T get(Class<T> key) {
        return key.cast(uncheckedMap.get(key));
    }

    public <T> T remove(Class<T> key) {
        return key.cast(uncheckedMap.remove(key));
    }

    public void clear() {
        uncheckedMap.clear();
    }

    public boolean containsKey(Class<?> key) {
        return uncheckedMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return uncheckedMap.containsValue(value);
    }
}
