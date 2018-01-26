package org.dimdev.ddutils;

import net.minecraft.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class MCPReflection {
    public static Field getMCPField(Class<?> class0, String deobfuscatedName, String obfuscatedName) throws NoSuchFieldException {
        Field field;
        try {
            field = class0.getDeclaredField(obfuscatedName);
        } catch (NoSuchFieldException e) { // Running on deobfuscated Minecraft
            field = class0.getDeclaredField(deobfuscatedName);
        }
        field.setAccessible(true);

        try {
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            return field;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getMCPMethod(Class<?> class0, String deobfuscatedName, String obfuscatedName, Class<?>... args) throws NoSuchMethodException {
        Method method;
        try {
            method = class0.getDeclaredMethod(obfuscatedName, args);
        } catch (NoSuchMethodException e) { // Running on deobfuscated Minecraft
            method = class0.getDeclaredMethod(deobfuscatedName, args);
        }
        method.setAccessible(true);
        return method;
    }
}
