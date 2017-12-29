package org.dimdev.ddutils.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTStorable { // TODO: move these back
    public void readFromNBT(NBTTagCompound nbt);
    public NBTTagCompound writeToNBT(NBTTagCompound nbt);
}
