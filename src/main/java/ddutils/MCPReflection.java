package ddutils;

import net.minecraft.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MCPReflection {
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
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return field;
    }

    public static Method getMCPMethod(Class<?> class0, String deobfuscatedName, String obfuscatedName, Class<?> args) throws NoSuchMethodException {
        Method method;
        try {
            method = Entity.class.getDeclaredMethod(obfuscatedName, args);
        } catch (NoSuchMethodException e) { // Running on deobfuscated Minecraft
            method = Entity.class.getDeclaredMethod(deobfuscatedName, args);
        }
        method.setAccessible(true);
        return method;
    }
}
