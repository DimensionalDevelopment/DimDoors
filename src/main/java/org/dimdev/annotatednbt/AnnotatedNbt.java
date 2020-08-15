package org.dimdev.annotatednbt;

import com.google.gson.Gson;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import org.dimdev.dimdoors.util.RotatedLocation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public final class AnnotatedNbt {
    public static Gson gson = new Gson();

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

    public static void fromTag(Object playerRiftPointer, CompoundTag nbt) {

    }

    public static CompoundTag toTag(Object playerRiftPointer, CompoundTag nbt) {
        return null;
    }

    public static Tag toTag(Object obj) {
        return new Dynamic<>(JsonOps.INSTANCE, gson.toJsonTree(obj)).convert(NbtOps.INSTANCE).getValue();
    }

    public static Object fromTag(Class<?> clazz, Tag nbt) {
        return gson.fromJson(new Dynamic<>(NbtOps.INSTANCE, nbt).convert(JsonOps.INSTANCE).getValue(), clazz);
    }
}
