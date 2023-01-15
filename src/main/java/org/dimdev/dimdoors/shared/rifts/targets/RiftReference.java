package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.util.math.Vec3i;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RGBA;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;

import java.util.Set;

/**
 * Allows rifts and targets to reference another rift without having to
 * actually load the rift's chunk and get its tile entity (which could lead
 * to recursively loading many chunks to load a single rift's chunk).
 * <p>
 * Information about the referenced rift's location is stored in the RiftRegistry
 * such that when the target rift is gone, the destination is notified and invalidated
 * (see shouldInvalidate)
 */
public abstract class RiftReference extends VirtualTarget {

    // Helper methods to create a certain type of reference, defaulting to
    // a global destination if not possible
    public static RiftReference tryMakeLocal(Location from, Location to) {
        if (from.getDim() != to.getDim()) {
            return new GlobalReference(to);
        } else {
            return new LocalReference(to.getPos());
        }
    }

    public static RiftReference tryMakeRelative(Location from, Location to) {
        if (from.getDim() != to.getDim()) {
            return new GlobalReference(to);
        } else {
            return new RelativeReference(new Vec3i(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()));
        }
    }

    public abstract Location getReferencedLocation();

    @Override
    public ITarget receiveOther() {
        return (ITarget) getReferencedLocation().getTileEntity();
    }

    @Override
    public void register() {
        RiftRegistry.instance().addLink(location, getReferencedLocation());
    }

    @Override
    public void unregister() {
        RiftRegistry.instance().removeLink(location, getReferencedLocation());
    }

    @Override
    public boolean shouldInvalidate(Location deletedRift) {
        // A rift we may have asked the registry to notify us about was deleted
        return deletedRift.equals(getReferencedLocation());
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public RGBA getColor() {
        Location target = getReferencedLocation();
        if (target != null && RiftRegistry.instance().isRiftAt(target)) {
            Set<Location> otherRiftTargets = RiftRegistry.instance().getTargets(target);
            if (otherRiftTargets.size() == 1 && otherRiftTargets.contains(location)) return new RGBA(0, 1, 0, 1);
        }
        return new RGBA(1, 0, 0, 1);
    }
}
