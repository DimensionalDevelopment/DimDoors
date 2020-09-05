package org.dimdev.dimdoors.rift.registry;

import java.util.UUID;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;

public abstract class RegistryVertex {
    public RegistryKey<World> world; // The dimension to store this object in. Links are stored in both registries.
    @Saved
    public UUID id = UUID.randomUUID(); // Used to create pointers to registry vertices. Should not be used for anything other than saving.

    public void sourceGone(RegistryVertex source) {
    }

    public void targetGone(RegistryVertex target) {
    }

    public void sourceAdded(RegistryVertex source) {
    }

    public void targetAdded(RegistryVertex target) {
    }

    public void fromTag(CompoundTag nbt) {
        AnnotatedNbt.fromTag(this, nbt);
    }

    public CompoundTag toTag(CompoundTag nbt) {
        return AnnotatedNbt.toTag(this, nbt);
    }

    public String toString() {
        return "RegistryVertex(dim=" + this.world + ", id=" + this.id + ")";
    }
}
