package org.dimdev.dimdoors.rift.targets;

import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.util.Location;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class LocalReference extends RiftReference {
    @Saved
    protected BlockPos target;

    public LocalReference(BlockPos target) {
        this.target = target;
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        AnnotatedNbt.load(this, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        return AnnotatedNbt.serialize(this);
    }

    @Override
    public Location getReferencedLocation() {
        return new Location(location.world, target);
    }

    public BlockPos getTarget() {
        return target;
    }
}
