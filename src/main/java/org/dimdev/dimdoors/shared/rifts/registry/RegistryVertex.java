package org.dimdev.dimdoors.shared.rifts.registry;

import org.dimdev.annotatednbt.Saved;

import java.util.UUID;

public abstract class RegistryVertex {
    public int dim; // The dimension to store this object in. Links are stored in both registries.
    @Saved public UUID id = UUID.randomUUID(); // Used to create pointers to registry vertices. Should not be used for anything other than saving.

    public void sourceGone(RegistryVertex source) {
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    public void targetGone(RegistryVertex target) {
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    public void sourceAdded(RegistryVertex to) {
        RiftRegistry.instance().markSubregistryDirty(dim);
    }

    public void targetAdded(RegistryVertex to) {
        RiftRegistry.instance().markSubregistryDirty(dim);
    }
}
