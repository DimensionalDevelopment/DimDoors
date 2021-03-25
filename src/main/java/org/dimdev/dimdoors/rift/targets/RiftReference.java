package org.dimdev.dimdoors.rift.targets;

import java.util.Set;

import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import net.minecraft.util.math.Vec3i;

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
	public RiftReference() {
	}

	// Helper methods to create a certain type of reference, defaulting to
	// a global destination if not possible
	public static RiftReference tryMakeLocal(Location from, Location to) {
		if (from.world != to.world) {
			return new GlobalReference(to);
		} else {
			return new LocalReference(to.pos);
		}
	}

	public static RiftReference tryMakeRelative(Location from, Location to) {
		if (from.world != to.world) {
			return new GlobalReference(to);
		} else {
			return new RelativeReference(new Vec3i(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()));
		}
	}

	public abstract Location getReferencedLocation();

	@Override
	public Target receiveOther() {
		return (Target) this.getReferencedLocation().getBlockEntity();
	}

	@Override
	public void register() {
		DimensionalRegistry.getRiftRegistry().addLink(this.location, this.getReferencedLocation());
	}

	@Override
	public void unregister() {
		DimensionalRegistry.getRiftRegistry().removeLink(this.location, this.getReferencedLocation());
	}

	@Override
	public boolean shouldInvalidate(Location deletedRift) {
		// A rift we may have asked the registry to notify us about was deleted
		return deletedRift.equals(this.getReferencedLocation());
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public RGBA getColor() {
		Location target = this.getReferencedLocation();
		if (target != null && DimensionalRegistry.getRiftRegistry().isRiftAt(target)) {
			Set<Location> otherRiftTargets = DimensionalRegistry.getRiftRegistry().getTargets(target);
			if (otherRiftTargets.size() == 1 && otherRiftTargets.contains(this.location)) {
				return new RGBA(0, 1, 0, 1);
			}
		}
		return new RGBA(1, 0, 0, 1);
	}
}
