package org.dimdev.dimdoors.util;

import org.apache.commons.lang3.tuple.Pair;
import org.dimdev.dimdoors.ModConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionHelper {
    public static <T extends Annotation> List<Field> fields(Object object, Class<T> annotation) {
        Class<?> clazz = object.getClass();

        var list = new ArrayList<Field>();

        for (Field field : clazz.getDeclaredFields()) {
            T instance = field.getAnnotation(annotation);

            if (instance != null) {
                field.setAccessible(true);
                list.add(field);
            }
        }

        return list;
    }

    public static Object getInstance(Field field, Object config) {
        try {
            return field.get(config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}