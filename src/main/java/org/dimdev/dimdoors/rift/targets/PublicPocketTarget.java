package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.nbt.CompoundTag;

public class PublicPocketTarget extends RestoringTarget {

	private VirtualTarget wrappedDestination = null;

	private PublicPocketTarget(VirtualTarget wrappedDestination) {
		this.wrappedDestination = wrappedDestination;
	}


	public PublicPocketTarget() {
	}

	@Override
	protected VirtualTarget getTarget() {
		return this.wrappedDestination;
	}

	@Override
	protected void setTarget(VirtualTarget target) {

	}

	@Override
	public Location makeLinkTarget() {
		VirtualLocation riftVirtualLocation = VirtualLocation.fromLocation(this.location);
		VirtualLocation newVirtualLocation;
		int depth = Math.max(riftVirtualLocation.getDepth(), 1);
		newVirtualLocation = new VirtualLocation(riftVirtualLocation.getWorld(), riftVirtualLocation.getX(), riftVirtualLocation.getZ(), depth);
		Pocket pocket = PocketGenerator.generatePublicPocketV2(newVirtualLocation, new GlobalReference(this.location), null);

		return RiftRegistry.instance().getPocketEntrance(pocket);
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.PUBLIC_POCKET;
	}

	public static CompoundTag toTag(PublicPocketTarget target) {
		CompoundTag tag = new CompoundTag();
		if (target.wrappedDestination != null)
			tag.put("wrappedDestination", VirtualTarget.toTag(target.wrappedDestination));
		return tag;
	}

	public static PublicPocketTarget fromTag(CompoundTag tag) {
		PublicPocketTarget target = new PublicPocketTarget();
		if (tag.contains("wrappedDestination"))
			target.wrappedDestination = VirtualTarget.fromTag(tag.getCompound("wrappedDestination"));
		return target;
	}
}
