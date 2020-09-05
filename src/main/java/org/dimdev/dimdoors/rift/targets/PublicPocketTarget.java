package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.nbt.CompoundTag;

public class PublicPocketTarget extends RestoringTarget {
    public final static Codec<PublicPocketTarget> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                VirtualTarget.CODEC.optionalFieldOf("wrappedDestination", null).forGetter(RestoringTarget::getTarget)
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
        return wrappedDestination;
    }

    @Override
    protected void setTarget(VirtualTarget target) {

    }

    @Override
    public Location makeLinkTarget() {
        VirtualLocation riftVirtualLocation = VirtualLocation.fromLocation(location);
        VirtualLocation newVirtualLocation;
        int depth = Math.max(riftVirtualLocation.depth, 1);
        newVirtualLocation = new VirtualLocation(riftVirtualLocation.world, riftVirtualLocation.x, riftVirtualLocation.z, depth);
        Pocket pocket = PocketGenerator.generatePublicPocket(newVirtualLocation, new GlobalReference(location), null);

        return RiftRegistry.instance().getPocketEntrance(pocket);
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.PUBLIC_POCKET;
    }
}
