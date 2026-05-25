package org.dimdev.dimdoors.client.config;

import org.dimdev.dimdoors.config.Category;
import org.dimdev.dimdoors.config.Option;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ConfigReflection {
    private ConfigReflection() {
    }

    public static List<CategoryInfo> scan(Object config) {
        Objects.requireNonNull(config, "config");

        List<CategoryInfo> categories = new ArrayList<>();
        Class<?> clazz = config.getClass();

        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (skipField(field)) {
                    continue;
                }

                Category category = field.getAnnotation(Category.class);

                if (category == null) {
                    continue;
                }

                field.setAccessible(true);
                Object value = get(field, config);

                if (value == null) {
                    continue;
                }

                List<OptionInfo<?>> options = new ArrayList<>();
                Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
                scanOptions(field.getName(), value, options, visited);

                categories.add(new CategoryInfo(field.getName(), field, config, value, category, options));
            }

            clazz = clazz.getSuperclass();
        }

        return List.copyOf(categories);
    }

    static void scanOptions(String path, Object owner, List<OptionInfo<?>> options, Set<Object> visited) {
        if (owner == null || !visited.add(owner)) {
            return;
        }

        Class<?> clazz = owner.getClass();

        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (skipField(field)) {
                    continue;
                }

                field.setAccessible(true);
                Option option = field.getAnnotation(Option.class);

                if (option != null) {
                    if (isExpandableObjectOption(field, owner)) {
                        Object nested = get(field, owner);

                        if (nested != null) {
                            scanOptions(path + "." + field.getName(), nested, options, visited);
                        } else {
                            options.add(OptionInfo.field(path + "." + field.getName(), field, owner, option));
                        }
                    } else {
                        options.add(OptionInfo.field(path + "." + field.getName(), field, owner, option));
                    }
                } else if (shouldDescend(field, owner)) {
                    Object nested = get(field, owner);

                    if (nested != null) {
                        scanOptions(path + "." + field.getName(), nested, options, visited);
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    private static boolean isExpandableObjectOption(Field field, Object owner) {
        Object value = get(field, owner);

        if (value == null) {
            return false;
        }

        return isExpandableConfigObjectType(value.getClass()) && containsOptionFields(value.getClass());
    }

    private static boolean shouldDescend(Field field, Object owner) {
        Object value = get(field, owner);

        if (value == null) {
            return false;
        }

        return isExpandableConfigObjectType(value.getClass()) && containsOptionFields(value.getClass());
    }

    private static boolean containsOptionFields(Class<?> clazz) {
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (skipField(field)) {
                    continue;
                }

                if (field.isAnnotationPresent(Option.class)) {
                    return true;
                }

                if (isExpandableConfigObjectType(field.getType()) && containsOptionFields(field.getType())) {
                    return true;
                }
            }

            current = current.getSuperclass();
        }

        return false;
    }

    public static boolean isExpandableConfigObjectType(Class<?> type) {
        if (type.isPrimitive() || type.isEnum() || type.isArray()) {
            return false;
        }

        if (type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || type == Character.class) {
            return false;
        }

        if (type.getName().startsWith("java.")) {
            return false;
        }

        if (type.getName().startsWith("net.minecraft.")) {
            return false;
        }

        if (List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
            return false;
        }

        return true;
    }

    private static Object get(Field field, Object owner) {
        try {
            field.setAccessible(true);
            return field.get(owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to read config field: " + field.getName(), e);
        }
    }

    private static boolean skipField(Field field) {
        int modifiers = field.getModifiers();

        return field.isSynthetic()
                || Modifier.isStatic(modifiers)
                || Modifier.isTransient(modifiers);
    }
}
