package org.dimdev.ddutils.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTStorable {
    public void readFromNBT(NBTTagCompound nbt);
    public NBTTagCompound writeToNBT(NBTTagCompound nbt);
}
