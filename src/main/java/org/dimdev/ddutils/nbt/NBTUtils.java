package org.dimdev.ddutils.nbt;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3i;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class NBTUtils {
    public static <T extends Map<String, Integer>> T readMapStringInteger(NBTTagCompound nbt, T map) {
        for (String str : nbt.getKeySet()) {
            map.put(str, nbt.getInteger(str));
        }
        return map;
    }

    public static NBTTagCompound writeMapStringInteger(Map<String, Integer> map) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        for (String str : map.keySet()) {
            tagCompound.setInteger(str, map.get(str));
        }
        return tagCompound;
    }

    public static <T extends INBTStorable> T readNBTStorable(T obj, NBTTagCompound nbt) {
        obj.readFromNBT(nbt);
        return obj;
    }

    public static Vec3i readVec3i(NBTTagCompound nbt) {
        return new Vec3i(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
    }

    public static NBTTagCompound writeVec3i(Vec3i vec) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", vec.getX());
        nbt.setInteger("y", vec.getY());
        nbt.setInteger("z", vec.getZ());
        return nbt;
    }
}
