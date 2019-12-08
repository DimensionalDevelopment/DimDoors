package org.dimdev.dimdoors.rift.targets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.util.Location;
import org.dimdev.annotatednbt.AnnotatedNbt;

public class LocalReference extends RiftReference {
    @Saved protected BlockPos target;

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

    public BlockPos getTarget() {return target;}
}
