package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.nbt.*;

public final class FlowTrackerNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.targets.FlowTracker obj, NBTTagCompound nbt) {
        // Write field java.util.Map<net.minecraft.util.EnumFacing,java.lang.Integer> redstone
        if (obj.redstone != null) {
            NBTTagList tag = new NBTTagList();
            for (java.util.Map.Entry<net.minecraft.util.EnumFacing,java.lang.Integer> element : obj.redstone.entrySet()) {
                NBTTagCompound elementNBT = new NBTTagCompound();
                NBTTagInt key = new NBTTagInt(element.getKey().ordinal());
                elementNBT.setTag("key", key);
                NBTTagInt value = new NBTTagInt(element.getValue());
                elementNBT.setTag("value", value);
                tag.appendTag(elementNBT);
            }
            nbt.setTag("redstone", tag);
        }

        // Write field java.util.Map<net.minecraft.util.EnumFacing,java.lang.Integer> power
        if (obj.power != null) {
            NBTTagList tag1 = new NBTTagList();
            for (java.util.Map.Entry<net.minecraft.util.EnumFacing,java.lang.Integer> element : obj.power.entrySet()) {
                NBTTagCompound elementNBT = new NBTTagCompound();
                NBTTagInt key = new NBTTagInt(element.getKey().ordinal());
                elementNBT.setTag("key", key);
                NBTTagInt value = new NBTTagInt(element.getValue());
                elementNBT.setTag("value", value);
                tag1.appendTag(elementNBT);
            }
            nbt.setTag("power", tag1);
        }
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.targets.FlowTracker obj, NBTTagCompound nbt) {
        // Read field java.util.Map<net.minecraft.util.EnumFacing,java.lang.Integer> redstone
        if (nbt.hasKey("redstone")) {
            NBTBase tag = nbt.getTag("redstone");
            java.util.Map<net.minecraft.util.EnumFacing,java.lang.Integer> arr = new java.util.HashMap<>();
            for (NBTBase elementNBT : (NBTTagList) tag) {
                net.minecraft.util.EnumFacing key = net.minecraft.util.EnumFacing.values()[((NBTTagInt) ((NBTTagCompound) elementNBT).getTag("key")).getInt()];
                java.lang.Integer value = ((NBTTagInt) ((NBTTagCompound) elementNBT).getTag("value")).getInt();
                arr.put(key, value);
            }
            obj.redstone = arr;
        }

        // Read field java.util.Map<net.minecraft.util.EnumFacing,java.lang.Integer> power
        if (nbt.hasKey("power")) {
            NBTBase tag1 = nbt.getTag("power");
            java.util.Map<net.minecraft.util.EnumFacing,java.lang.Integer> arr1 = new java.util.HashMap<>();
            for (NBTBase elementNBT : (NBTTagList) tag1) {
                net.minecraft.util.EnumFacing key = net.minecraft.util.EnumFacing.values()[((NBTTagInt) ((NBTTagCompound) elementNBT).getTag("key")).getInt()];
                java.lang.Integer value = ((NBTTagInt) ((NBTTagCompound) elementNBT).getTag("value")).getInt();
                arr1.put(key, value);
            }
            obj.power = arr1;
        }
    }
}
