package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.forge.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.forge.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.forge.world.pocket.type.Pocket;

public class PublicPocketTarget extends WrappedDestinationTarget {
	public static final Codec<PublicPocketTarget> CODEC = RecordCodecBuilder.create(instance -> instance.group(VirtualTarget.CODEC.optionalFieldOf("wrappedDestination", null).forGetter(a -> a.wrappedDestination)).apply(instance, PublicPocketTarget::new));

	private PublicPocketTarget(VirtualTarget wrappedDestination) {
		super(wrappedDestination);
	}

	public PublicPocketTarget() {
		super();
	}

	@Override
	public Location makeLinkTarget() {
		VirtualLocation riftVirtualLocation = VirtualLocation.fromLocation(this.location);
		VirtualLocation newVirtualLocation;
		int depth = riftVirtualLocation.getDepth() + 1;
		newVirtualLocation = new VirtualLocation(riftVirtualLocation.getWorld(), riftVirtualLocation.getX(), riftVirtualLocation.getZ(), depth);
		Pocket pocket = PocketGenerator.generatePublicPocketV2(newVirtualLocation, new GlobalReference(this.location), null);

		return DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket);
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.PUBLIC_POCKET.get();
	}

	@Override
	public VirtualTarget copy() {
		return new PublicPocketTarget(wrappedDestination);
	}
}
