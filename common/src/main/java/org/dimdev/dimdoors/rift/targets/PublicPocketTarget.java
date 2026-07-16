package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.MapCodec;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class PublicPocketTarget extends RestoringTarget<PublicPocketTarget> {
    public static final MapCodec<PublicPocketTarget> CODEC = MapCodec.unit(PublicPocketTarget::of);

    public static PublicPocketTarget of() {
        return new PublicPocketTarget();
    }

    @Override
    public Location makeLinkTarget() {
        VirtualLocation riftVirtualLocation = VirtualLocation.fromLocation(this.location);
        VirtualLocation newVirtualLocation;
        int depth = riftVirtualLocation.getDepth() + 1;
        newVirtualLocation = new VirtualLocation(riftVirtualLocation.getWorld(), riftVirtualLocation.getX(), riftVirtualLocation.getZ(), depth);
        Pocket<?, ?> pocket = PocketGenerator.generatePublicPocketV2(newVirtualLocation, new RiftReference(this.location), null);

        return DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket);
    }

    @Override
    public VirtualTargetType<PublicPocketTarget> getType() {
        return VirtualTargetType.PUBLIC_POCKET;
    }

    @Override
    public PublicPocketTarget copy() {
        return new PublicPocketTarget();
    }
}