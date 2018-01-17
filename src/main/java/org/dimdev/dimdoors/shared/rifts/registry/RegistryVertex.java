package org.dimdev.dimdoors.shared.rifts.registry;

import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.DimDoors;

import java.util.UUID;

@ToString
@NBTSerializable public abstract class RegistryVertex implements INBTStorable {
    public int dim; // The dimension to store this object in. Links are stored in both registries.
    @Saved public UUID id = UUID.randomUUID(); // Used to create pointers to registry vertices. Should not be used for anything other than saving.

    public void sourceGone(RegistryVertex source) {
        DimDoors.log.info("Notified vertex " + this + " that source " + source + " is gone");
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    public void targetGone(RegistryVertex target) {
        DimDoors.log.info("Notified vertex " + this + " that target " + target + " is gone");
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    public void sourceAdded(RegistryVertex source) {
        DimDoors.log.info("Notified vertex " + this + " that source " + source + " was added");
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    public void targetAdded(RegistryVertex target) {
        DimDoors.log.info("Notified vertex " + this + " that target " + target + " was added");
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }
}
