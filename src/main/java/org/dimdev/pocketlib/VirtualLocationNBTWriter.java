package org.dimdev.pocketlib;

import net.minecraft.nbt.NBTTagCompound;

public final class VirtualLocationNBTWriter {

    public static void writeToNBT(org.dimdev.pocketlib.VirtualLocation obj, NBTTagCompound nbt) {
        // Write field int dim
        nbt.setInteger("dim", obj.dim);

        // Write field int x
        nbt.setInteger("x", obj.x);

        // Write field int z
        nbt.setInteger("z", obj.z);

        // Write field int depth
        nbt.setInteger("depth", obj.depth);
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.pocketlib.VirtualLocation obj, NBTTagCompound nbt) {
        // Read field int dim
        obj.dim = nbt.getInteger("dim");

        // Read field int x
        obj.x = nbt.getInteger("x");

        // Read field int z
        obj.z = nbt.getInteger("z");

        // Read field int depth
        obj.depth = nbt.getInteger("depth");
    }
}
