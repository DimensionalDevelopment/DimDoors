package com.zixiken.dimdoors.shared.util;

import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;
public class NBTUtils {
    public static Map<String, Integer> readMapStringInteger(NBTTagCompound nbt) {
        HashMap<String, Integer> map = new HashMap<>();
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
}
