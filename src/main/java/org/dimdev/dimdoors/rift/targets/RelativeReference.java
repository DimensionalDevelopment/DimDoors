package org.dimdev.dimdoors.rift.targets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3i;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.util.Location;

public class RelativeReference extends RiftReference {
    @Saved protected Vec3i offset;

    public RelativeReference(Vec3i offset) {
        this.offset = offset;
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        AnnotatedNbt.load(this, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        AnnotatedNbt.save(this, tag);
        return tag;
    }

    @Override
    public Location getReferencedLocation() {
        return new Location(location.world, location.pos.add(offset));
    }

    public Vec3i getOffset() {
        return offset;
    }
}
