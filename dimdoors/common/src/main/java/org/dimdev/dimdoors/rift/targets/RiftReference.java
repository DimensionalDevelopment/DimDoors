package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.MapCodec;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.rift.target.TargetResolver;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;

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
public class RiftReference extends VirtualTarget<RiftReference> implements LocationProvider {
    public static MapCodec<RiftReference> CODEC = Location.CODEC.fieldOf("target").xmap(RiftReference::new, RiftReference::getLocation);

    private final Location target;

    public RiftReference(Location target) {
        this.target = target;
    }


    @Override
    public Location getLocation() {
        return target;
    }

    @Override
    public Target receiveOther() {
        return TargetResolver.target(this.target);
    }

    @Override
    public void register() {
        RiftRegistry.getInstance().addLink(this.location, this.target);
    }

    @Override
    public void unregister() {
        if (this.location != null)
            RiftRegistry.getInstance().removeLink(this.location, this.target);
    }

    @Override
    public boolean shouldInvalidate(Location deletedRift) {
        // A rift we may have asked the registry to notify us about was deleted
        return deletedRift.equals(this.target);
    }

    @Override
    public RGBA getColor() {
        if (target != null && RiftRegistry.getInstance().isRiftAt(target)) {
            Set<Location> otherRiftTargets = RiftRegistry.getInstance().getTargets(target);
            if (otherRiftTargets.size() == 1 && otherRiftTargets.contains(this.location)) {
                return new RGBA(0, 1, 0, 1);
            }
        }
        return new RGBA(1, 0, 0, 1);
    }

    @Override
    public VirtualTargetType<RiftReference> getType() {
        return VirtualTargetType.RIFT_REFERENCE;
    }

    @Override
    public RiftReference copy() {
        return new RiftReference(getLocation());
    }
}
