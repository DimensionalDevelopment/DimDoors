package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RGBA;

public abstract class RestoringTarget extends VirtualTarget {

    private VirtualTarget wrappedDestination;

    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        wrappedDestination = nbt.hasKey("wrappedDestination") ? VirtualTarget.readVirtualTargetNBT(nbt.getCompoundTag("wrappedDestination")) : null;
    }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        if (wrappedDestination != null) nbt.setTag("wrappedDestination", wrappedDestination.writeToNBT(new NBTTagCompound()));
        return nbt;
    }

    @Override
    public ITarget receiveOther() {
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
    public RGBA getColor() {
        if (wrappedDestination != null) {
            return wrappedDestination.getColor();
        } else {
            return getUnlinkedColor(location);
        }
    }

    protected RGBA getUnlinkedColor(Location location) {
        return new RGBA(0, 1, 1, 1);
    }

    public abstract Location makeLinkTarget();
}
