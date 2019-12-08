package org.dimdev.dimdoors.rift.registry;

import net.minecraft.nbt.CompoundTag;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.annotatednbt.AnnotatedNbt;

import java.util.UUID;

public abstract class RegistryVertex {
    public int dim; // The dimension to store this object in. Links are stored in both registries.
    @Saved public UUID id = UUID.randomUUID(); // Used to create pointers to registry vertices. Should not be used for anything other than saving.

    public void sourceGone(RegistryVertex source) {
        LOGGER.debug("Notified vertex " + this + " that source " + source + " is gone");
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    public void targetGone(RegistryVertex target) {
        LOGGER.debug("Notified vertex " + this + " that target " + target + " is gone");
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    public void sourceAdded(RegistryVertex source) {
        LOGGER.debug("Notified vertex " + this + " that source " + source + " was added");
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    public void targetAdded(RegistryVertex target) {
        LOGGER.debug("Notified vertex " + this + " that target " + target + " was added");
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    @Override
    public void fromTag(CompoundTag nbt) { AnnotatedNbt.fromTag(this, nbt); }

    @Override
    public CompoundTag toTag(CompoundTag nbt) { return AnnotatedNbt.toTag(this, nbt); }

    public String toString() {return "RegistryVertex(dim=" + this.dim + ", id=" + this.id + ")";}
}
