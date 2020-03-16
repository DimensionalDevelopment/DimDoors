package org.dimdev.dimdoors.rift.targets;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;

public class PocketExitMarker extends VirtualTarget implements EntityTarget {
    public PocketExitMarker() {}

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        return nbt;
    }

    @Override
    public boolean receiveEntity(Entity entity, float yawOffset) {
        entity.sendMessage(new TranslatableText("The exit of this dungeon has not been linked. If this is a normally generated pocket, please report this bug."));
        return false;
    }
}
