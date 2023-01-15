package org.dimdev.pocketlib;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;

import java.util.Map;
import java.util.Objects;

public final class PocketRegistryNBTWriter {

    public static void writeToNBT(org.dimdev.pocketlib.PocketRegistry obj, NBTTagCompound nbt) {
        // Write field int gridSize
        nbt.setInteger("gridSize", obj.gridSize);

        // Write field int privatePocketSize
        nbt.setInteger("privatePocketSize", obj.privatePocketSize);

        // Write field int publicPocketSize
        nbt.setInteger("publicPocketSize", obj.publicPocketSize);

        // Write field java.util.Map<java.lang.Integer,org.dimdev.pocketlib.Pocket> pockets
        if (Objects.nonNull(obj.pockets)) {
            NBTTagList tag = new NBTTagList();
            for (Map.Entry<java.lang.Integer,org.dimdev.pocketlib.Pocket> element : obj.pockets.entrySet()) {
                NBTTagCompound elementNBT = new NBTTagCompound();
                NBTTagInt key = new NBTTagInt(element.getKey());
                elementNBT.setTag("key", key);
                NBTTagCompound value = element.getValue().writeToNBT(new NBTTagCompound());
                elementNBT.setTag("value", value);
                tag.appendTag(elementNBT);
            }
            nbt.setTag("pockets", tag);
        }

        // Write field int nextID
        nbt.setInteger("nextID", obj.nextID);
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.pocketlib.PocketRegistry obj, NBTTagCompound nbt) {
        // Read field int gridSize
        obj.gridSize = nbt.getInteger("gridSize");

        // Read field int privatePocketSize
        obj.privatePocketSize = nbt.getInteger("privatePocketSize");

        // Read field int publicPocketSize
        obj.publicPocketSize = nbt.getInteger("publicPocketSize");

        // Read field java.util.Map<java.lang.Integer,org.dimdev.pocketlib.Pocket> pockets
        if (nbt.hasKey("pockets")) {
            NBTBase tag = nbt.getTag("pockets");
            java.util.Map<java.lang.Integer,org.dimdev.pocketlib.Pocket> arr = new java.util.HashMap<>();
            for (NBTBase elementNBT : (NBTTagList) tag) {
                java.lang.Integer key = ((NBTTagInt) ((NBTTagCompound) elementNBT).getTag("key")).getInt();
                org.dimdev.pocketlib.Pocket value = new org.dimdev.pocketlib.Pocket();
                value.readFromNBT((NBTTagCompound) ((NBTTagCompound) elementNBT).getTag("value"));
                arr.put(key, value);
            }
            obj.pockets = arr;
        }

        // Read field int nextID
        obj.nextID = nbt.getInteger("nextID");
    }
}
