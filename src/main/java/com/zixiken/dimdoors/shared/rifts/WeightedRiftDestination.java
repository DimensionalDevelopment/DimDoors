package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.shared.util.INBTStorable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.NBTTagCompound;

@NoArgsConstructor @AllArgsConstructor
public class WeightedRiftDestination implements INBTStorable {
    @Getter private RiftDestination destination;
    @Getter private float weight;
    @Getter private int group;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        destination = RiftDestination.readDestinationNBT(nbt.getCompoundTag("destination"));
        weight = nbt.getFloat("weight");
        group = nbt.getInteger("group");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("destination", destination.writeToNBT(nbt));
        nbt.setFloat("weight", weight);
        nbt.setInteger("group", group);
        return nbt;
    }
}
