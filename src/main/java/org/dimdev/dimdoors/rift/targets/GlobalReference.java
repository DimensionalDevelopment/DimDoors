package org.dimdev.dimdoors.rift.targets;

import net.minecraft.nbt.CompoundTag;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.util.Location;
import org.dimdev.annotatednbt.AnnotatedNbt;

public class GlobalReference extends RiftReference {
    @Saved protected Location target;

    public GlobalReference() {};

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
