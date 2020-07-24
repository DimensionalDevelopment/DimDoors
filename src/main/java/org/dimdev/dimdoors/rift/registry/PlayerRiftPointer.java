package org.dimdev.dimdoors.rift.registry;

import java.util.UUID;

import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;

import net.minecraft.nbt.CompoundTag;

public class PlayerRiftPointer extends RegistryVertex {
    @Saved
    public UUID player;

    public PlayerRiftPointer(UUID player) {
        this.player = player;
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        AnnotatedNbt.fromTag(this, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        return AnnotatedNbt.toTag(this, nbt);
    }

    public String toString() {
        return "PlayerRiftPointer(player=" + this.player + ")";
    }
}
