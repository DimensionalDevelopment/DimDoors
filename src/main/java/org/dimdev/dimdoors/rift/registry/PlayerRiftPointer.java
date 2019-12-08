package org.dimdev.dimdoors.rift.registry;

import net.minecraft.nbt.CompoundTag;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.AnnotatedNbt;

import java.util.UUID;

public class PlayerRiftPointer extends RegistryVertex {
    @Saved public UUID player;

    public PlayerRiftPointer(UUID player) {
        this.player = player;
    }

    public PlayerRiftPointer() {}

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

    public String toString() {return "PlayerRiftPointer(player=" + this.player + ")";}
}
