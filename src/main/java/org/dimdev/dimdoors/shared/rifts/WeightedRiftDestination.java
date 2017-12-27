package org.dimdev.dimdoors.shared.rifts;

import org.dimdev.ddutils.nbt.INBTStorable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.NBTTagCompound;

@NoArgsConstructor
public class WeightedRiftDestination implements INBTStorable { // TODO: generics
    @Getter private RiftDestination destination;
    @Getter private float weight;
    @Getter private int group;
    @Getter private RiftDestination oldDestination; // TODO: move to RiftDestination?

    public WeightedRiftDestination(RiftDestination destination, float weight, int group, RiftDestination oldDestination) {
        this();
        this.destination = destination;
        this.weight = weight;
        this.group = group;
        this.oldDestination = oldDestination;
        if (destination != null) destination.weightedDestination = this;
        if (oldDestination != null) oldDestination.weightedDestination = this;
    }

    public WeightedRiftDestination(RiftDestination destination, float weight, int group) {
        this(destination, weight, group, null);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        destination = RiftDestination.readDestinationNBT(nbt); // TODO: subtag?
        weight = nbt.getFloat("weight");
        group = nbt.getInteger("group");
        if (nbt.hasKey("oldDestination")) oldDestination = RiftDestination.readDestinationNBT(nbt.getCompoundTag("oldDestination"));
        if (destination != null) destination.weightedDestination = this;
        if (oldDestination != null) oldDestination.weightedDestination = this;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = destination.writeToNBT(nbt);
        nbt.setFloat("weight", weight);
        nbt.setInteger("group", group);
        if (oldDestination != null) nbt.setTag("oldDestination", oldDestination.writeToNBT(new NBTTagCompound()));
        return nbt;
    }
}
