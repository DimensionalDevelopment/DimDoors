package org.dimdev.annotatednbt;

import net.minecraft.nbt.CompoundTag;

public interface AutoSerializable {
    default void save(CompoundTag tag) {
        AnnotatedNbt.save(this, tag);
    }

    default CompoundTag serialize() {
        return AnnotatedNbt.serialize(this);
    }

    default void load(CompoundTag tag) {
        AnnotatedNbt.load(this, tag);
    }
}
