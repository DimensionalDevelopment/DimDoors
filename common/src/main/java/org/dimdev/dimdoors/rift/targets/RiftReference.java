package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.MapCodec;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

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
public class RiftReference extends VirtualTarget implements LocationProvider {
    public static MapCodec<RiftReference> CODEC = Location.CODEC.fieldOf("target").xmap(RiftReference::new, RiftReference::getLocation);

    private final Location target;

    public RiftReference(Location target) {
        this.target = target;
    }


    @Override
    public Location   getLocation() {
        return target;
    }

    @Override
    public Target receiveOther() {
        return this.target.getBlockEntity() instanceof Target beTarget ? beTarget : null;
    }

    @Override
    public void register() {
        DimensionalRegistry.getRiftRegistry().addLink(this.location, this.target);
    }

    @Override
    public void unregister() {
        if (this.location != null)
            DimensionalRegistry.getRiftRegistry().removeLink(this.location, this.target);
    }

    @Override
    public boolean shouldInvalidate(Location deletedRift) {
        // A rift we may have asked the registry to notify us about was deleted
        return deletedRift.equals(this.target);
    }

    @Override
    public RGBA getColor() {
        if (target != null && DimensionalRegistry.getRiftRegistry().isRiftAt(target)) {
            Set<Location> otherRiftTargets = DimensionalRegistry.getRiftRegistry().getTargets(target);
            if (otherRiftTargets.size() == 1 && otherRiftTargets.contains(this.location)) {
                return new RGBA(0, 1, 0, 1);
            }
        }
        return new RGBA(1, 0, 0, 1);
    }

    @Override
    public VirtualTargetType<?> getType() {
        return VirtualTargetType.RIFT_REFENCE;
    }

    @Override
    public VirtualTarget copy() {
        return new RiftReference(target);
    }
}