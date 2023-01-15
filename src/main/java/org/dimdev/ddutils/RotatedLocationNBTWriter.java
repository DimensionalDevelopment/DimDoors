package org.dimdev.ddutils;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

@SuppressWarnings("UnnecessaryLocalVariable")
public final class RotatedLocationNBTWriter {

    public static void writeToNBT(org.dimdev.ddutils.RotatedLocation obj, NBTTagCompound nbt) {
        // Write field org.dimdev.ddutils.Location location
        if (obj.location != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dim", obj.location.getDim());
            tag.setInteger("x", obj.location.getX());
            tag.setInteger("y", obj.location.getY());
            tag.setInteger("z", obj.location.getZ());
            nbt.setTag("location", tag);
        }
        // Write field float yaw
        nbt.setFloat("yaw", obj.yaw);
        // Write field float pitch
        nbt.setFloat("pitch", obj.pitch);
    }

    public static void readFromNBT(org.dimdev.ddutils.RotatedLocation obj, NBTTagCompound nbt) {
        // Read field org.dimdev.ddutils.Location location
        if (nbt.hasKey("location")) {
            NBTBase tag = nbt.getTag("location");
            org.dimdev.ddutils.Location arr = new org.dimdev.ddutils.Location(((NBTTagCompound) tag).getInteger("dim"), ((NBTTagCompound) tag).getInteger("x"), ((NBTTagCompound) tag).getInteger("y"), ((NBTTagCompound) tag).getInteger("z"));
            obj.location = arr;
        }
        // Read field float yaw
        obj.yaw = nbt.getFloat("yaw");
        // Read field float pitch
        obj.pitch = nbt.getFloat("pitch");
    }
}
