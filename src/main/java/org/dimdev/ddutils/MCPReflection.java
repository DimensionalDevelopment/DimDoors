package org.dimdev.ddutils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;

public final class MCPReflection {
    
    public static MethodHandle getHandle(
            Lookup lookup, Class<?> clazz, String name, String srgName, Class<?>... args)
            throws ReflectiveOperationException {
        Method method;
        try {
            method = clazz.getDeclaredMethod(srgName, args);
        } catch(Exception ignored) {
            method = clazz.getDeclaredMethod(name, args);
        }
        method.setAccessible(true);
        return lookup.unreflect(method);
    }
}