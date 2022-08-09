package org.dimdev.pocketlib;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public final class PrivatePocketDataNBTWriter {

    public static void writeToNBT(org.dimdev.pocketlib.PrivatePocketData obj, NBTTagCompound nbt) {
        // Write field com.google.common.collect.BiMap<java.lang.String,org.dimdev.pocketlib.PrivatePocketData.PocketInfo> privatePocketMap
        if (obj.privatePocketMap != null) {
            NBTTagList tag = new NBTTagList();
            for (java.util.Map.Entry<java.lang.String,org.dimdev.pocketlib.PrivatePocketData.PocketInfo> element : obj.privatePocketMap.entrySet()) {
                NBTTagCompound elementNBT = new NBTTagCompound();
                NBTTagString key = new NBTTagString(element.getKey());
                elementNBT.setTag("key", key);
                NBTTagCompound value = element.getValue().writeToNBT(new NBTTagCompound());
                elementNBT.setTag("value", value);
                tag.appendTag(elementNBT);
            }
            nbt.setTag("privatePocketMap", tag);
        }
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.pocketlib.PrivatePocketData obj, NBTTagCompound nbt) {
        // Read field com.google.common.collect.BiMap<java.lang.String,org.dimdev.pocketlib.PrivatePocketData.PocketInfo> privatePocketMap
        if (nbt.hasKey("privatePocketMap")) {
            NBTBase tag = nbt.getTag("privatePocketMap");
            com.google.common.collect.BiMap<java.lang.String,org.dimdev.pocketlib.PrivatePocketData.PocketInfo> arr = com.google.common.collect.HashBiMap.create();
            for (NBTBase elementNBT : (NBTTagList) tag) {
                java.lang.String key = ((NBTTagString) ((NBTTagCompound) elementNBT).getTag("key")).getString();
                org.dimdev.pocketlib.PrivatePocketData.PocketInfo value = new org.dimdev.pocketlib.PrivatePocketData.PocketInfo();
                value.readFromNBT((NBTTagCompound) ((NBTTagCompound) elementNBT).getTag("value"));
                arr.put(key, value);
            }
            obj.privatePocketMap = arr;
        }
    }
}