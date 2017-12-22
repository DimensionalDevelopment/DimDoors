package com.zixiken.dimdoors.shared.rifts;

import ddutils.nbt.INBTStorable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.NBTTagCompound;

@NoArgsConstructor @AllArgsConstructor
public class
WeightedRiftDestination implements INBTStorable { // TODO: generics
    @Getter private RiftDestination destination;
    @Getter private float weight;
    @Getter private int group;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        destination = RiftDestination.readDestinationNBT(nbt); // TODO: subtag?
        weight = nbt.getFloat("weight");
        group = nbt.getInteger("group");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = destination.writeToNBT(nbt);
        nbt.setFloat("weight", weight);
        nbt.setInteger("group", group);
        return nbt;
    }
}
