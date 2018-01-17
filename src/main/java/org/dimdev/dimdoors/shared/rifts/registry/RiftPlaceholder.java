package org.dimdev.dimdoors.shared.rifts.registry;

import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.dimdoors.DimDoors;

@ToString
@NBTSerializable public class RiftPlaceholder extends Rift { // TODO: don't extend rift

    @Override public void sourceGone(RegistryVertex source) {}

    @Override public void targetGone(RegistryVertex target) {}

    @Override public void sourceAdded(RegistryVertex source) {}

    @Override public void targetAdded(RegistryVertex target) {}

    @Override public void targetChanged(RegistryVertex target) {}

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        DimDoors.log.warn("Reading a rift placeholder from NBT!");
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        DimDoors.log.warn("Writing a rift placeholder from NBT!");
        return super.writeToNBT(nbt);
    }
}
