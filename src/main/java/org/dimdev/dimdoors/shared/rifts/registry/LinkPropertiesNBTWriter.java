package org.dimdev.dimdoors.shared.rifts.registry;

import net.minecraft.nbt.*;

public final class LinkPropertiesNBTWriter {

    public static void writeToNBT(org.dimdev.dimdoors.shared.rifts.registry.LinkProperties obj, NBTTagCompound nbt) {
        // Write field float floatingWeight
        nbt.setFloat("floatingWeight", obj.floatingWeight);

        // Write field float entranceWeight
        nbt.setFloat("entranceWeight", obj.entranceWeight);

        // Write field java.util.Set<java.lang.Integer> groups
        if (obj.groups != null) {
            NBTTagList tag = new NBTTagList();
            for (java.lang.Integer element : obj.groups) {
                NBTTagInt elementNBT = new NBTTagInt(element);
                tag.appendTag(elementNBT);
            }
            nbt.setTag("groups", tag);
        }

        // Write field int linksRemaining
        nbt.setInteger("linksRemaining", obj.linksRemaining);

        // Write field boolean oneWay
        nbt.setBoolean("oneWay", obj.oneWay);
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.dimdoors.shared.rifts.registry.LinkProperties obj, NBTTagCompound nbt) {
        // Read field float floatingWeight
        obj.floatingWeight = nbt.getFloat("floatingWeight");

        // Read field float entranceWeight
        obj.entranceWeight = nbt.getFloat("entranceWeight");

        // Read field java.util.Set<java.lang.Integer> groups
        if (nbt.hasKey("groups")) {
            NBTBase tag = nbt.getTag("groups");
            java.util.Set<java.lang.Integer> arr = new java.util.HashSet<>();
            for (NBTBase elementNBT : (NBTTagList) tag) {
                java.lang.Integer element = ((NBTTagInt) elementNBT).getInt();
                arr.add(element);
            }
            obj.groups = arr;
        }

        // Read field int linksRemaining
        obj.linksRemaining = nbt.getInteger("linksRemaining");

        // Read field boolean oneWay
        obj.oneWay = nbt.getBoolean("oneWay");
    }
}
