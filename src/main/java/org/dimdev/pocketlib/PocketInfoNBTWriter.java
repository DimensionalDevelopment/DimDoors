package org.dimdev.pocketlib;

import net.minecraft.nbt.NBTTagCompound;

public final class PocketInfoNBTWriter {

    public static void writeToNBT(org.dimdev.pocketlib.PrivatePocketData.PocketInfo obj, NBTTagCompound nbt) {
        // Write field int dim
        nbt.setInteger("dim", obj.dim);

        // Write field int id
        nbt.setInteger("id", obj.id);
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.pocketlib.PrivatePocketData.PocketInfo obj, NBTTagCompound nbt) {
        // Read field int dim
        obj.dim = nbt.getInteger("dim");

        // Read field int id
        obj.id = nbt.getInteger("id");
    }
}
