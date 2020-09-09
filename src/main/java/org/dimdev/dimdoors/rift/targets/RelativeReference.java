package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.util.Location;

import net.minecraft.util.math.Vec3i;

public class RelativeReference extends RiftReference {

    private Vec3i offset;

    public static Codec<RelativeReference> CODEC = Vec3i.field_25123.xmap(RelativeReference::new, RelativeReference::getOffset).fieldOf("offset").codec();

    public RelativeReference(Vec3i offset) {
        this.offset = offset;
    }

    @Override
    public Location getReferencedLocation() {
        return new Location(location.world, location.pos.add(offset));
    }

    public Vec3i getOffset() {
        return offset;
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.RELATIVE;
    }
}
