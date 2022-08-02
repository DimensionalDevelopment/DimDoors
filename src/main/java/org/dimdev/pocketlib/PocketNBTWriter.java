package org.dimdev.pocketlib;

import net.minecraft.nbt.NBTTagCompound;

@SuppressWarnings("ConstantConditions")
public final class PocketNBTWriter {

    public static void writeToNBT(org.dimdev.pocketlib.Pocket obj, NBTTagCompound nbt) {
        // Write field int id
        nbt.setInteger("id", obj.id);

        // Write field int x
        nbt.setInteger("x", obj.x);

        // Write field int z
        nbt.setInteger("z", obj.z);

        // Write field int size
        nbt.setInteger("size", obj.size);

        // Write field org.dimdev.pocketlib.VirtualLocation virtualLocation
        if (obj.virtualLocation != null) {
            if (obj.virtualLocation != null) nbt.setTag("virtualLocation", obj.virtualLocation.writeToNBT(new NBTTagCompound()));
        }
    }

    @SuppressWarnings({"OverlyStrongTypeCast", "RedundantSuppression"})
    public static void readFromNBT(org.dimdev.pocketlib.Pocket obj, NBTTagCompound nbt) {
        // Read field int id
        obj.id = nbt.getInteger("id");

        // Read field int x
        obj.x = nbt.getInteger("x");

        // Read field int z
        obj.z = nbt.getInteger("z");

        // Read field int size
        obj.size = nbt.getInteger("size");

        // Read field org.dimdev.pocketlib.VirtualLocation virtualLocation
        if (nbt.hasKey("virtualLocation")) {
            obj.virtualLocation = new org.dimdev.pocketlib.VirtualLocation();
            obj.virtualLocation.readFromNBT(nbt.getCompoundTag("virtualLocation"));
        }
    }
}