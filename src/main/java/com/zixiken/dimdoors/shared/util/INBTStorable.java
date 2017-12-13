package com.zixiken.dimdoors.shared.util;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTStorable {
    public void readFromNBT(NBTTagCompound nbt);
    public NBTTagCompound writeToNBT(NBTTagCompound nbt);
}
