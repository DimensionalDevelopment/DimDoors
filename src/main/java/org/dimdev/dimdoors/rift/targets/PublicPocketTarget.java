package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PublicPocketTarget extends RestoringTarget {
    public final static Codec<PublicPocketTarget> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                VirtualTarget.CODEC.optionalFieldOf("wrappedDestination", NoneTarget.DUMMY).forGetter(RestoringTarget::getTarget)
        ).apply(instance, PublicPocketTarget::new);
    });

    private VirtualTarget wrappedDestination;

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
}
