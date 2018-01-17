package org.dimdev.dimdoors.shared.rifts.destinations;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RGBA;
import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;

public abstract class LinkingDestination extends RiftDestination {

    private RiftDestination wrappedDestination;

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return nbt; }

    @Override
    public boolean teleport(RotatedLocation loc, Entity entity) {
        if (wrappedDestination != null) wrappedDestination.teleport(loc, entity);

        Location linkTarget = makeLinkTarget(loc, entity);
        if (linkTarget != null) {
            wrappedDestination = new GlobalDestination();
            wrappedDestination.register(loc.getLocation());

            wrappedDestination.teleport(loc, entity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean keepAfterTargetGone(Location location, Location target) {
        if (!wrappedDestination.keepAfterTargetGone(location, target)) {
            wrappedDestination.unregister(location);
        }
        return true;
    }

    @Override
    public void unregister(Location location) {
        if (wrappedDestination != null) wrappedDestination.unregister(location);
    }

    @Override
    public RGBA getColor(Location location) {
        if (wrappedDestination != null) {
            return wrappedDestination.getColor(location);
        } else {
            return getUnlinkedColor(location);
        }
    }

    protected RGBA getUnlinkedColor(Location location) {
        return new RGBA(0, 1, 1, 1);
    }

    public abstract Location makeLinkTarget(RotatedLocation rift, Entity entity);
}
