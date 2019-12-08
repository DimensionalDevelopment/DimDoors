package org.dimdev.annotatednbt;

import net.minecraft.nbt.CompoundTag;
import org.dimdev.util.RotatedLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class AnnotatedNbt {
    public static <T> T deserialize(Class<RotatedLocation> rotatedLocationClass, CompoundTag tag) {
        return null; // TODO
    }

    public static CompoundTag serialize(Object object) {
        return null; // TODO
    }

    public static void load(Object object, CompoundTag tag) {
        // TODO
    }

    public static void save(Object object, CompoundTag tag) {
        // TODO
    }
}
