package org.dimdev.ddutils.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTStorable {
    void readFromNBT(NBTTagCompound nbt);
    NBTTagCompound writeToNBT(NBTTagCompound nbt);
}
