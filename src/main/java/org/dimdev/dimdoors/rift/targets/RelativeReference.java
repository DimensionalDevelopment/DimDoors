package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.util.Location;
import com.mojang.serialization.Codec;

import net.minecraft.util.math.Vec3i;

public class RelativeReference extends RiftReference {

    private final Vec3i offset;

    public static Codec<RelativeReference> CODEC = Vec3i.CODEC.xmap(RelativeReference::new, RelativeReference::getOffset).fieldOf("offset").codec();

    public RelativeReference(Vec3i offset) {
        this.offset = offset;
    }

    @Override
    public Location getReferencedLocation() {
        return new Location(this.location.world, this.location.pos.add(this.offset));
    }

    public Vec3i getOffset() {
        return this.offset;
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.RELATIVE;
    }
}
