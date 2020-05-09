package org.dimdev.dimdoors.rift.targets;

import net.minecraft.nbt.CompoundTag;
import org.dimdev.util.Location;

public abstract class RestoringTarget extends VirtualTarget {

    private VirtualTarget wrappedDestination;

    public RestoringTarget() {};

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        wrappedDestination = nbt.contains("wrappedDestination") ? VirtualTarget.readVirtualTargetNBT(nbt.getCompound("wrappedDestination")) : null;
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        if (wrappedDestination != null) nbt.put("wrappedDestination", wrappedDestination.toTag(new CompoundTag()));
        return nbt;
    }

    @Override
    public Target receiveOther() {
        if (wrappedDestination != null) {
            wrappedDestination.location = location;
            return wrappedDestination;
        }

        Location linkTarget = makeLinkTarget();
        if (linkTarget != null) {
            wrappedDestination = RiftReference.tryMakeLocal(location, linkTarget);
            wrappedDestination.setLocation(location);
            wrappedDestination.register();

            return wrappedDestination;
        } else {
            return null;
        }
    }

    @Override
    public boolean shouldInvalidate(Location deletedRift) {
        if (wrappedDestination.shouldInvalidate(deletedRift)) {
            wrappedDestination.unregister();
        }
        return false;
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        if (wrappedDestination != null) {
            wrappedDestination.setLocation(location);
        }
    }

    @Override
    public void unregister() {
        if (wrappedDestination != null) wrappedDestination.unregister();
    }

    @Override
    public float[] getColor() {
        if (wrappedDestination != null) {
            wrappedDestination.location = location;
            return wrappedDestination.getColor();
        } else {
            return getUnlinkedColor(location);
        }
    }

    protected float[] getUnlinkedColor(Location location) {
        return new float[]{0, 1, 1, 1};
    }

    public abstract Location makeLinkTarget();
}
