package org.dimdev.dimdoors.rift.targets;

import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.util.Location;

import net.minecraft.nbt.CompoundTag;

public class GlobalReference extends RiftReference {
    @Saved
    protected Location target;

    public GlobalReference(Location target) {
        this.target = target;
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        AnnotatedNbt.save(this, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        return AnnotatedNbt.serialize(this);
    }

    @Override
    public Location getReferencedLocation() {
        return target;
    }
}
